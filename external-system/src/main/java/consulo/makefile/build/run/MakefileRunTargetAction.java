/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.makefile.build.run;

import com.jetbrains.lang.makefile.MakefileUtil;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.execution.ExecutionUtil;
import consulo.execution.RunManager;
import consulo.execution.RunnerAndConfigurationSettings;
import consulo.execution.configuration.ConfigurationTypeUtil;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.executor.DefaultRunExecutor;
import consulo.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import consulo.externalSystem.service.execution.AbstractExternalSystemTaskConfigurationType;
import consulo.externalSystem.service.execution.ExternalSystemRunConfiguration;
import consulo.language.psi.PsiFile;
import consulo.makefile.build.MakeConstants;
import consulo.makefile.build.MakeProjectPaths;
import consulo.makefile.build.service.MakeExternalTaskConfigurationType;
import consulo.makefile.build.setting.MakeProjectSettings;
import consulo.makefile.build.setting.MakeSettings;
import consulo.makefile.localize.MakefileLocalize;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Port of the JetBrains {@code MakefileRunTargetAction}: runs one target of a makefile. The JetBrains action creates
 * a {@code MakefileRunConfiguration}; here the target runs as a task of the Make external system - in the linked
 * project the makefile belongs to, with its build directory and arguments, or else in the makefile's directory.
 */
public class MakefileRunTargetAction extends AnAction {
    private final MakefileTarget myTarget;

    public MakefileRunTargetAction(MakefileTarget target) {
        super(MakefileLocalize.actionRunTargetText(target.getName()),
            MakefileLocalize.actionRunTargetDescription(target.getName()),
            MakefileUtil.MAKEFILE_TARGET_ICON);
        getTemplatePresentation().setDisabledMnemonic(true);
        myTarget = target;
    }

    @Override
    @RequiredUIAccess
    public void actionPerformed(AnActionEvent event) {
        Project project = myTarget.getProject();
        PsiFile file = myTarget.getContainingFile();
        VirtualFile virtualFile = file != null ? file.getVirtualFile() : null;
        if (virtualFile == null) {
            return;
        }
        Path makefile = Path.of(virtualFile.getPath());
        Path directory = makefile.getParent();
        if (directory == null) {
            return;
        }

        ExternalSystemTaskExecutionSettings taskSettings = new ExternalSystemTaskExecutionSettings();
        taskSettings.setExternalSystemIdString(MakeConstants.SYSTEM_ID.getId());
        taskSettings.setExternalProjectPath(findExternalProjectPath(project, directory));
        taskSettings.setTaskNames(List.of(myTarget.getName()));
        // make reads a makefile of another name only when told to
        if (!makefile.equals(MakeProjectPaths.findMakefile(directory))) {
            taskSettings.setScriptParameters(ParametersListUtil.join("-f", makefile.toString()));
        }

        RunManager runManager = RunManager.getInstance(project);
        MakeExternalTaskConfigurationType type = ConfigurationTypeUtil.findConfigurationType(MakeExternalTaskConfigurationType.class);
        RunnerAndConfigurationSettings configuration = findExisting(runManager, type, taskSettings);
        if (configuration == null) {
            configuration = runManager.createRunConfiguration(
                AbstractExternalSystemTaskConfigurationType.generateName(project, taskSettings), type.getFactory());
            ((ExternalSystemRunConfiguration) configuration.getConfiguration()).setSettings(taskSettings);
            configuration.setTemporary(true);
            runManager.addConfiguration(configuration);
        }
        runManager.setSelectedConfiguration(configuration);

        ExecutionUtil.runConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance());
    }

    /**
     * The linked Make project of the makefile's directory - it carries the build directory and the arguments - or else
     * the directory itself.
     */
    private static String findExternalProjectPath(Project project, Path directory) {
        for (MakeProjectSettings settings : MakeSettings.getInstance(project).getLinkedProjectsSettings()) {
            if (MakeProjectPaths.getSourceDir(settings.getExternalProjectPath()).equals(directory)) {
                return settings.getExternalProjectPath();
            }
        }
        return directory.toString();
    }

    private static @Nullable RunnerAndConfigurationSettings findExisting(RunManager runManager,
                                                                         MakeExternalTaskConfigurationType type,
                                                                         ExternalSystemTaskExecutionSettings taskSettings) {
        for (RunnerAndConfigurationSettings settings : runManager.getConfigurationSettingsList(type)) {
            RunConfiguration configuration = settings.getConfiguration();
            if (!(configuration instanceof ExternalSystemRunConfiguration externalConfiguration)) {
                continue;
            }
            ExternalSystemTaskExecutionSettings existing = externalConfiguration.getSettingsCopy();
            if (existing.getExternalProjectPath().equals(taskSettings.getExternalProjectPath())
                && existing.getTaskNames().equals(taskSettings.getTaskNames())
                && Objects.equals(existing.getScriptParameters(), taskSettings.getScriptParameters())) {
                return settings;
            }
        }
        return null;
    }
}
