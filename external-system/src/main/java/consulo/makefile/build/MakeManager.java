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
package consulo.makefile.build;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.build.service.MakeProjectResolver;
import consulo.makefile.build.service.MakeTaskManager;
import consulo.makefile.build.setting.MakeExecutionSettings;
import consulo.makefile.build.setting.MakeLocalSettings;
import consulo.makefile.build.setting.MakeProjectSettings;
import consulo.makefile.build.setting.MakeSettings;
import consulo.makefile.build.setting.MakeSettingsListener;
import consulo.externalSystem.ExternalSystemManager;
import consulo.externalSystem.model.ProjectSystemId;
import consulo.externalSystem.service.project.ExternalSystemProjectResolver;
import consulo.externalSystem.service.project.autoimport.CachingExternalSystemAutoImportAware;
import consulo.externalSystem.service.project.autoimport.ExternalSystemAutoImportAware;
import consulo.externalSystem.task.ExternalSystemTaskManager;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.project.Project;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeManager implements ExternalSystemAutoImportAware,
    ExternalSystemManager<MakeProjectSettings, MakeSettingsListener, MakeSettings, MakeLocalSettings, MakeExecutionSettings> {
    private final MakeAutoImportAware myAutoImportAware = new MakeAutoImportAware();

    private final ExternalSystemAutoImportAware myAutoImportDelegate = new CachingExternalSystemAutoImportAware(myAutoImportAware);

    @Override
    public ProjectSystemId getSystemId() {
        return MakeConstants.SYSTEM_ID;
    }

    @Override
    public Function<Project, MakeSettings> getSettingsProvider() {
        return MakeSettings::getInstance;
    }

    @Override
    public Function<Project, MakeLocalSettings> getLocalSettingsProvider() {
        return MakeLocalSettings::getInstance;
    }

    @Override
    public Function<Pair<Project, String>, MakeExecutionSettings> getExecutionSettingsProvider() {
        return pair -> {
            MakeProjectSettings settings = MakeSettings.getInstance(pair.getFirst()).getLinkedProjectSettings(pair.getSecond());
            if (settings == null) {
                return new MakeExecutionSettings(null, "", "");
            }
            return new MakeExecutionSettings(settings.getBuildDirectory(), valueOrEmpty(settings.getMakeArguments()),
                valueOrEmpty(settings.getConfigureArguments()));
        };
    }

    private static String valueOrEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    @Override
    public Supplier<? extends ExternalSystemProjectResolver<MakeExecutionSettings>> getProjectResolverFactory() {
        return MakeProjectResolver::new;
    }

    @Override
    public Supplier<? extends ExternalSystemTaskManager<MakeExecutionSettings>> getTaskManagerFactory() {
        return MakeTaskManager::new;
    }

    @Override
    public FileChooserDescriptor getExternalProjectDescriptor() {
        return new FileChooserDescriptor(true, true, false, false, false, false) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return file.isDirectory() || MakeProjectPaths.isProjectFile(file.getName());
            }
        };
    }

    @Override
    public @Nullable String getAffectedExternalProjectPath(String changedFileOrDirPath, Project project) {
        return myAutoImportDelegate.getAffectedExternalProjectPath(changedFileOrDirPath, project);
    }

    @Override
    public List<Path> getAffectedExternalProjectFilePaths(String projectPath, Project project) {
        return myAutoImportAware.getAffectedExternalProjectFilePaths(projectPath, project);
    }
}
