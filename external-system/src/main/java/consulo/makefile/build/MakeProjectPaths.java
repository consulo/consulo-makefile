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

import consulo.makefile.build.discovery.CompilationDatabase;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Where a Make project is: the linked path names its makefile, configure script or directory.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class MakeProjectPaths {
    private MakeProjectPaths() {
    }

    public static Path getSourceDir(String projectPath) {
        Path path = Path.of(projectPath).toAbsolutePath().normalize();
        if (Files.isDirectory(path)) {
            return path;
        }
        Path parent = path.getParent();
        return parent != null ? parent : path;
    }

    /**
     * The directory make runs in: the configured one, relative to the sources, or the sources themselves - an in-tree
     * build, which every makefile supports.
     */
    public static Path getBuildDir(Path sourceDir, @Nullable String buildDirectory) {
        if (buildDirectory == null || buildDirectory.isBlank()) {
            return sourceDir;
        }
        return sourceDir.resolve(buildDirectory.trim()).normalize();
    }

    public static @Nullable Path findMakefile(Path directory) {
        for (String name : MakeConstants.MAKEFILE_NAMES) {
            Path makefile = directory.resolve(name);
            if (Files.isRegularFile(makefile)) {
                return makefile;
            }
        }
        return null;
    }

    /**
     * Whether a file is one a Make project can be imported from.
     */
    public static boolean isProjectFile(String fileName) {
        return MakeConstants.MAKEFILE_NAMES.contains(fileName)
            || MakeConstants.CONFIGURE.equals(fileName)
            || MakeConstants.CONFIGURE_SOURCES.contains(fileName)
            || CompilationDatabase.FILE_NAME.equals(fileName);
    }

    /**
     * Whether a directory holds a Make project: a makefile, a configure script, what autoconf makes one from, or a
     * compilation database.
     */
    public static boolean isProjectDirectory(Path directory) {
        if (findMakefile(directory) != null || Files.isRegularFile(directory.resolve(MakeConstants.CONFIGURE))) {
            return true;
        }
        for (String name : MakeConstants.CONFIGURE_SOURCES) {
            if (Files.isRegularFile(directory.resolve(name))) {
                return true;
            }
        }
        return Files.isRegularFile(directory.resolve(CompilationDatabase.FILE_NAME));
    }
}
