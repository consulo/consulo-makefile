/* AdvancedTools, 2007, all rights reserved */
package com.advancedtools.cpp.makefile;

import consulo.language.Language;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;
import jakarta.annotation.Nonnull;

/**
 * @author maxim
 */
public class MakefileLanguage extends Language {
    public static final Language INSTANCE = new MakefileLanguage();

    public MakefileLanguage() {
        super("Makefile");
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return MakefileLocalize.makefileText();
    }
}
