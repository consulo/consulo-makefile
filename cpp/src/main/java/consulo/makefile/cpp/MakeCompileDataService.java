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
package consulo.makefile.cpp;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.cpp.module.MakeCMutableModuleExtension;
import consulo.cpp.module.extension.CCompileSettings;
import consulo.makefile.build.service.MakeCompileData;
import consulo.externalSystem.model.DataNode;
import consulo.externalSystem.model.Key;
import consulo.externalSystem.model.ProjectKeys;
import consulo.externalSystem.model.project.ModuleData;
import consulo.externalSystem.service.project.manage.ProjectDataService;
import consulo.externalSystem.util.DisposeAwareProjectChange;
import consulo.externalSystem.util.ExternalSystemApiUtil;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.project.Project;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Applies the compile settings of the imported groups to their modules.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeCompileDataService implements ProjectDataService<MakeCompileData, Module> {
    @Override
    public Key<MakeCompileData> getTargetDataKey() {
        return MakeCompileData.KEY;
    }

    @Override
    public void importData(Collection<DataNode<MakeCompileData>> toImport, Project project, boolean synchronous) {
        if (toImport.isEmpty()) {
            return;
        }
        Map<String, MakeCompileSettingsCollector> collectors = new LinkedHashMap<>();
        for (DataNode<MakeCompileData> node : toImport) {
            ModuleData moduleData = node.getData(ProjectKeys.MODULE);
            if (moduleData != null) {
                collectors.computeIfAbsent(moduleData.getInternalName(), name -> new MakeCompileSettingsCollector()).add(node.getData());
            }
        }
        ExternalSystemApiUtil.executeProjectChangeAction(synchronous, new DisposeAwareProjectChange(project) {
            @Override
            public void execute() {
                ModuleManager moduleManager = ModuleManager.getInstance(project);
                for (Map.Entry<String, MakeCompileSettingsCollector> entry : collectors.entrySet()) {
                    Module module = moduleManager.findModuleByName(entry.getKey());
                    if (module != null && !module.isDisposed()) {
                        apply(module, entry.getValue().build());
                    }
                }
            }
        });
    }

    private static void apply(Module module, CCompileSettings settings) {
        ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
        MakeCMutableModuleExtension extension = rootModel.getExtensionWithoutCheck(MakeCMutableModuleExtension.class);
        if (extension == null) {
            rootModel.dispose();
            return;
        }
        extension.setEnabled(true);
        extension.setCompileSettings(settings);
        rootModel.commit();
    }

    @Override
    public void removeData(Collection<? extends Module> toRemove, Project project, boolean synchronous) {
    }
}
