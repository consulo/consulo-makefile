package com.jetbrains.lang.makefile;

import consulo.language.Language;

public class MakefileLanguage extends Language {
    public static final MakefileLanguage INSTANCE = new MakefileLanguage();

    private MakefileLanguage() {
        super("Makefile");
    }
}
