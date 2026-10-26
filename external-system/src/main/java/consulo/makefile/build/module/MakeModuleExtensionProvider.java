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
package consulo.makefile.build.module;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.build.MakeConstants;
import consulo.makefile.icon.MakeFileIconGroup;
import consulo.makefile.build.localize.MakeBuildLocalize;
import consulo.externalSystem.service.module.extension.ExternalSystemModuleExtensionImpl;
import consulo.externalSystem.service.module.extension.ExternalSystemMutableModuleExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;
import org.jspecify.annotations.Nullable;

/**
 * Marks a module as imported from make; its id is the external system's.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeModuleExtensionProvider implements ModuleExtensionProvider<ExternalSystemModuleExtensionImpl> {
    @Override
    public String getId() {
        return MakeConstants.SYSTEM_ID.getId();
    }

    @Override
    public @Nullable String getParentId() {
        return null;
    }

    @Override
    public boolean isSystemOnly() {
        return true;
    }

    @Override
    public LocalizeValue getName() {
        return MakeBuildLocalize.makeSystemName();
    }

    @Override
    public Image getIcon() {
        return MakeFileIconGroup.makefile();
    }

    @Override
    public ModuleExtension<ExternalSystemModuleExtensionImpl> createImmutableExtension(ModuleRootLayer layer) {
        return new ExternalSystemModuleExtensionImpl(getId(), layer, MakeConstants.SYSTEM_ID);
    }

    @Override
    public MutableModuleExtension<ExternalSystemModuleExtensionImpl> createMutableExtension(ModuleRootLayer layer) {
        return new ExternalSystemMutableModuleExtensionImpl(getId(), layer, MakeConstants.SYSTEM_ID);
    }
}
