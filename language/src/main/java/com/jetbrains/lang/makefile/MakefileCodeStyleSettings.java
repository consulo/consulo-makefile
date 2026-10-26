package com.jetbrains.lang.makefile;

import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.CustomCodeStyleSettings;

public class MakefileCodeStyleSettings extends CustomCodeStyleSettings {
    public MakefileCodeStyleSettings(CodeStyleSettings container) {
        super(MakefileLanguage.INSTANCE.getID(), container);
    }
}
