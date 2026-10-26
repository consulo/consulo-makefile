package com.jetbrains.lang.makefile;

import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.makefile.icon.MakeFileIconGroup;
import consulo.makefile.localize.MakefileLocalize;
import consulo.ui.image.Image;

public class MakefileFileType extends LanguageFileType {
    public static final MakefileFileType INSTANCE = new MakefileFileType();

    private MakefileFileType() {
        super(MakefileLanguage.INSTANCE);
    }

    @Override
    public Image getIcon() {
        return MakeFileIconGroup.makefile();
    }

    @Override
    public String getId() {
        return "Makefile";
    }

    @Override
    public LocalizeValue getDescription() {
        return MakefileLocalize.filetypeMakefileDescription();
    }

    @Override
    public String getDefaultExtension() {
        return "mk";
    }
}
