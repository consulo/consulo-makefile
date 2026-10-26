package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.Configurable;
import consulo.language.Language;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.CustomCodeStyleSettings;
import consulo.language.codeStyle.setting.CodeStyleSettingsProvider;
import consulo.language.codeStyle.ui.setting.CodeStyleAbstractConfigurable;
import consulo.language.codeStyle.ui.setting.CodeStyleAbstractPanel;
import consulo.language.codeStyle.ui.setting.TabbedLanguageCodeStylePanel;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class MakefileCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @Override
    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(settings, modelSettings, MakefileLocalize.configurableName()) {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new TabbedLanguageCodeStylePanel(MakefileLanguage.INSTANCE, getCurrentSettings(), settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        addIndentOptionsTab(settings);
                    }
                };
            }

            @Override
            public @Nullable String getHelpTopic() {
                return null;
            }
        };
    }

    @Override
    public LocalizeValue getConfigurableDisplayName() {
        return MakefileLocalize.configurableName();
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }

    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new MakefileCodeStyleSettings(settings);
    }
}
