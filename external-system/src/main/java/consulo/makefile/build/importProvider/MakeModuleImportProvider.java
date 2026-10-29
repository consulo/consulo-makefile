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
package consulo.makefile.build.importProvider;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.build.MakeConstants;
import consulo.makefile.build.MakeProjectPaths;
import consulo.makefile.icon.MakeFileIconGroup;
import consulo.externalSystem.importing.AbstractExternalModuleImportProvider;
import consulo.externalSystem.importing.ExternalModuleImportContext;
import consulo.externalSystem.model.DataNode;
import consulo.externalSystem.service.project.ProjectData;
import consulo.externalSystem.service.project.manage.ProjectDataManager;
import consulo.project.Project;
import consulo.ui.image.Image;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Imports a project built with make - also one which has no makefile yet, only a configure script or what autoconf
 * makes one from: the import configures it first.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeModuleImportProvider extends AbstractExternalModuleImportProvider {
    @Inject
    public MakeModuleImportProvider(ProjectDataManager dataManager) {
        super(dataManager, MakeConstants.SYSTEM_ID);
    }

    @Override
    public @Nullable Image getIcon() {
        return MakeFileIconGroup.makefile();
    }

    @Override
    public boolean canImport(Path fileOrDirectory) {
        if (Files.isDirectory(fileOrDirectory)) {
            return MakeProjectPaths.isProjectDirectory(fileOrDirectory);
        }
        return MakeProjectPaths.isProjectFile(fileOrDirectory.getFileName().toString());
    }

    @Override
    protected void doPrepare(ExternalModuleImportContext context) {
    }

    @Override
    protected void beforeCommit(DataNode<ProjectData> dataNode, Project project) {
    }

    /**
     * The project is its directory - the makefile may be generated, by configure, in another one.
     */
    @Override
    protected File getExternalProjectConfigToUse(File file) {
        if (file.isDirectory()) {
            return file;
        }
        File parent = file.getParentFile();
        return parent != null ? parent : file;
    }

    @Override
    protected void applyExtraSettings(ExternalModuleImportContext context) {
    }
}
