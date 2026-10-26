package com.jetbrains.lang.makefile.psi;

import com.jetbrains.lang.makefile.MakefileLanguage;
import consulo.language.ast.IElementType;

public class MakefileTokenType extends IElementType {
    public MakefileTokenType(String debugName) {
        super(debugName, MakefileLanguage.INSTANCE);
    }
}
