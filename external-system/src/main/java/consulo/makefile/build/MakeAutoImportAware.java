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

import consulo.makefile.build.setting.MakeProjectSettings;
import consulo.makefile.build.setting.MakeSettings;
import consulo.externalSystem.service.project.autoimport.ExternalSystemAutoImportAware;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A change of a makefile, of an automake {@code Makefile.am} or of what configure is made from asks to import again.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeAutoImportAware implements ExternalSystemAutoImportAware {
    private static final Set<String> BUILD_FILES = Set.of(
        "GNUmakefile", "makefile", "Makefile", "Makefile.am", "Makefile.in", "configure.ac", "configure.in"
    );

    @Override
    public @Nullable String getAffectedExternalProjectPath(String changedFileOrDirPath, Project project) {
        Path changed = Path.of(changedFileOrDirPath).toAbsolutePath().normalize();
        Path fileName = changed.getFileName();
        if (fileName == null || !isBuildFile(fileName.toString()) || Files.isDirectory(changed)) {
            return null;
        }

        for (MakeProjectSettings settings : MakeSettings.getInstance(project).getLinkedProjectsSettings()) {
            String projectPath = settings.getExternalProjectPath();
            if (projectPath == null) {
                continue;
            }
            Path sourceDir = MakeProjectPaths.getSourceDir(projectPath);
            Path buildDir = MakeProjectPaths.getBuildDir(sourceDir, settings.getBuildDirectory());
            // configure writes the makefiles of the build directory; they are not edits
            if (!buildDir.equals(sourceDir) && changed.startsWith(buildDir)) {
                continue;
            }
            if (changed.startsWith(sourceDir)) {
                return projectPath;
            }
        }
        return null;
    }

    @Override
    public List<Path> getAffectedExternalProjectFilePaths(String projectPath, Project project) {
        Path sourceDir = MakeProjectPaths.getSourceDir(projectPath);
        List<Path> result = new ArrayList<>();
        for (String name : BUILD_FILES) {
            Path file = sourceDir.resolve(name);
            if (Files.isRegularFile(file)) {
                result.add(file);
            }
        }
        return result;
    }

    private static boolean isBuildFile(String fileName) {
        return BUILD_FILES.contains(fileName) || fileName.endsWith(".mk");
    }
}
