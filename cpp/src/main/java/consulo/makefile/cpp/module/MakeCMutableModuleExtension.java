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
package consulo.makefile.cpp.module;

import consulo.makefile.cpp.localize.MakeCLocalize;
import consulo.cpp.module.extension.CCompileSettings;
import consulo.cpp.lang.version.CLanguageVersion;
import consulo.cpp.module.extension.CModuleExtension;
import consulo.cpp.module.extension.CMutableModuleExtension;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeCMutableModuleExtension extends MakeCModuleExtension implements CMutableModuleExtension<MakeCModuleExtension> {
    private static final int MAX_LISTED_DEFINES = 20;

    public MakeCMutableModuleExtension(String id, ModuleRootLayer rootLayer) {
        super(id, rootLayer);
    }

    @Override
    public void setDefines(Map<String, String> defines) {
        myCompileSettings.setDefines(defines);
    }

    @Override
    public void setIncludePaths(List<String> includePaths) {
        myCompileSettings.setIncludePaths(includePaths);
    }

    @Override
    public void setTarget(@Nullable String target) {
        myCompileSettings.setTarget(target);
    }

    public void setCompileSettings(CCompileSettings settings) {
        myCompileSettings.copyFrom(settings);
    }

    @Override
    public void setEnabled(boolean enabled) {
        myIsEnabled = enabled;
    }

    @Override
    public boolean isModified(MakeCModuleExtension originalExtension) {
        return myIsEnabled != originalExtension.isEnabled() || !myCompileSettings.equals(originalExtension.myCompileSettings);
    }

    @RequiredUIAccess
    @Override
    public @Nullable Component createConfigurationComponent(Disposable uiDisposable, Runnable updateOnCheck) {
        return FormBuilder.create()
            .addLabeled(MakeCLocalize.extensionDefinesLabel(), Label.create(definesText(getDefines())))
            .addLabeled(MakeCLocalize.extensionIncludePathsLabel(), Label.create(LocalizeValue.of(CModuleExtension.formatIncludePaths(getIncludePaths()))))
            .addLabeled(MakeCLocalize.extensionCStandardLabel(), Label.create(LocalizeValue.of(standardName(getCStandard()))))
            .addLabeled(MakeCLocalize.extensionCppStandardLabel(), Label.create(LocalizeValue.of(standardName(getCppStandard()))))
            .build();
    }

    /**
     * The defines, or how many there are - with what the compiler predefines they are hundreds.
     */
    private static LocalizeValue definesText(Map<String, String> defines) {
        return defines.size() > MAX_LISTED_DEFINES
            ? MakeCLocalize.extensionDefinesCount(defines.size())
            : LocalizeValue.of(CModuleExtension.formatDefines(defines));
    }

    private static String standardName(@Nullable CLanguageVersion standard) {
        return standard == null ? "" : standard.getPresentableName();
    }
}
