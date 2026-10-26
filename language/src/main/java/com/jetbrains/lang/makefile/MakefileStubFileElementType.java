package com.jetbrains.lang.makefile;

import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.PsiFileStub;

public class MakefileStubFileElementType extends IStubFileElementType<PsiFileStub<MakefileFile>> {
    public MakefileStubFileElementType() {
        super("makefile", MakefileLanguage.INSTANCE);
    }
}
