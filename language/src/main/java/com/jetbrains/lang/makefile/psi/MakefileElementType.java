package com.jetbrains.lang.makefile.psi;

import com.jetbrains.lang.makefile.MakefileLanguage;
import consulo.language.ast.IElementType;

public class MakefileElementType extends IElementType {
    public MakefileElementType(String debugName) {
        super(debugName, MakefileLanguage.INSTANCE);
    }
}
