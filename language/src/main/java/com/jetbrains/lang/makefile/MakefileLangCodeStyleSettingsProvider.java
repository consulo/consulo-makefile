package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.codeStyle.CommonCodeStyleSettings;
import consulo.language.codeStyle.setting.CodeStyleSettingsCustomizable;
import consulo.language.codeStyle.setting.IndentOptionsEditor;
import consulo.language.codeStyle.setting.LanguageCodeStyleSettingsProvider;

@ExtensionImpl
public class MakefileLangCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings commonSettings = new CommonCodeStyleSettings(getLanguage());
        customizeDefaults(commonSettings, commonSettings.initIndentOptions());
        return commonSettings;
    }

    protected void customizeDefaults(CommonCodeStyleSettings commonSettings, CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.TAB_SIZE = 4;
        indentOptions.USE_TAB_CHARACTER = true;
    }

    @Override
    public void customizeSettings(CodeStyleSettingsCustomizable consumer, SettingsType settingsType) {
        if (settingsType == SettingsType.INDENT_SETTINGS) {
            consumer.showStandardOptions("TAB_SIZE");
        }
    }

    @Override
    public String getCodeSample(SettingsType settingsType) {
        return """
            # Simple Makefile
            include make.mk

            all: hello

            GCC = gcc \\
                       -O2

            <target>.o.c</target>:
            ifeq ($(FOO),'bar')
            \t$(GCC) -c qwe \\
                          -Wall
            else
            \techo "Hello World"
            endif""";
    }

    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new IndentOptionsEditor();
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
