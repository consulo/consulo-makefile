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

import consulo.configurable.ConfigurationException;
import consulo.makefile.build.localize.MakeBuildLocalize;
import consulo.makefile.build.setting.MakeProjectSettings;
import consulo.disposer.Disposable;
import consulo.externalSystem.service.setting.AbstractExternalProjectSettingsConfigurable;
import consulo.externalSystem.service.setting.ExternalSystemSettingsPlace;
import consulo.ui.Component;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeProjectSettingsConfigurable extends AbstractExternalProjectSettingsConfigurable<MakeProjectSettings> {
    private @Nullable TextBox myBuildDirectoryBox;
    private @Nullable TextBox myMakeArgumentsBox;
    private @Nullable TextBox myConfigureArgumentsBox;

    public MakeProjectSettingsConfigurable(MakeProjectSettings settings, ExternalSystemSettingsPlace place) {
        super(settings, place);
    }

    @Override
    @RequiredUIAccess
    protected Component createExtraUIComponent(Disposable uiDisposable) {
        MakeProjectSettings settings = getSettings();
        TextBox buildDirectoryBox = TextBox.create(valueOrEmpty(settings.getBuildDirectory()));
        buildDirectoryBox.setPlaceholder(MakeBuildLocalize.buildDirectoryPlaceholder());
        TextBox makeArgumentsBox = TextBox.create(valueOrEmpty(settings.getMakeArguments()));
        TextBox configureArgumentsBox = TextBox.create(valueOrEmpty(settings.getConfigureArguments()));
        myBuildDirectoryBox = buildDirectoryBox;
        myMakeArgumentsBox = makeArgumentsBox;
        myConfigureArgumentsBox = configureArgumentsBox;

        return FormBuilder.create()
            .addLabeled(MakeBuildLocalize.buildDirectoryLabel(), buildDirectoryBox)
            .addLabeled(MakeBuildLocalize.makeArgumentsLabel(), makeArgumentsBox)
            .addLabeled(MakeBuildLocalize.configureArgumentsLabel(), configureArgumentsBox)
            .build();
    }

    @Override
    @RequiredUIAccess
    protected boolean isExtraModified() {
        MakeProjectSettings settings = getSettings();
        return !Objects.equals(value(myBuildDirectoryBox), settings.getBuildDirectory())
            || !Objects.equals(value(myMakeArgumentsBox), settings.getMakeArguments())
            || !Objects.equals(value(myConfigureArgumentsBox), settings.getConfigureArguments());
    }

    @Override
    @RequiredUIAccess
    protected void applyExtra() throws ConfigurationException {
        MakeProjectSettings settings = getSettings();
        settings.setBuildDirectory(value(myBuildDirectoryBox));
        settings.setMakeArguments(value(myMakeArgumentsBox));
        settings.setConfigureArguments(value(myConfigureArgumentsBox));
    }

    /**
     * The text of a box, {@code null} when blank - as the settings keep it.
     */
    @RequiredUIAccess
    private static @Nullable String value(@Nullable TextBox box) {
        if (box == null) {
            return null;
        }
        // an empty box has no value
        String text = box.getValue();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private static String valueOrEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }
}
