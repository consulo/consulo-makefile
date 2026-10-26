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
package consulo.makefile.build.service;

import consulo.makefile.build.MakeProjectPaths;
import consulo.makefile.build.discovery.ShellWords;
import consulo.makefile.build.localize.MakeBuildLocalize;
import consulo.makefile.build.setting.MakeExecutionSettings;
import consulo.externalSystem.model.task.ExternalSystemTaskId;
import consulo.externalSystem.model.task.ExternalSystemTaskNotificationListener;
import consulo.externalSystem.rt.model.ExternalSystemException;
import consulo.externalSystem.task.ExternalSystemTaskManager;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

/**
 * Builds make targets: one make run for all the targets of a task, in the build directory.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeTaskManager implements ExternalSystemTaskManager<MakeExecutionSettings> {
    private final MakeProcessRunner myRunner = new MakeProcessRunner();

    @Override
    public void executeTasks(ExternalSystemTaskId id,
                             List<String> taskNames,
                             String projectPath,
                             @Nullable MakeExecutionSettings settings,
                             List<String> vmOptions,
                             List<String> scriptParameters,
                             @Nullable String debuggerSetup,
                             ExternalSystemTaskNotificationListener listener) throws ExternalSystemException {
        Path buildDir = MakeProjectPaths.getBuildDir(MakeProjectPaths.getSourceDir(projectPath),
            settings != null ? settings.getBuildDirectory() : null);
        List<String> makeArguments = settings == null ? List.of() : ShellWords.splitWords(settings.getMakeArguments(), command -> null);

        GeneralCommandLine commandLine = MakeProjectResolver.makeCommandLine(buildDir, makeArguments);
        commandLine.addParameters(scriptParameters);
        commandLine.addParameters(taskNames);
        listener.onTaskOutput(id, "Running: " + commandLine.getCommandLineString() + "\n", true);

        int exitCode;
        try {
            exitCode = myRunner.run(commandLine, (text, stdout) -> listener.onTaskOutput(id, text, stdout));
        }
        catch (ExecutionException e) {
            throw new ExternalSystemException(MakeBuildLocalize.errorFailedRun(commandLine.getExePath(), String.valueOf(e.getMessage())).get());
        }
        if (exitCode == MakeProcessRunner.CANCELLED_EXIT_CODE) {
            throw new ExternalSystemException(MakeBuildLocalize.errorCancelled().get());
        }
        if (exitCode != 0) {
            throw new ExternalSystemException(MakeBuildLocalize.errorExitCode(commandLine.getExePath(), exitCode).get());
        }
    }

    @Override
    public boolean cancelTask(ExternalSystemTaskId id, ExternalSystemTaskNotificationListener listener) throws ExternalSystemException {
        myRunner.cancel();
        return true;
    }
}
