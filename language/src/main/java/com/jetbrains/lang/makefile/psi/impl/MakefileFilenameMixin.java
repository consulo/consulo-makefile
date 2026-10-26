package com.jetbrains.lang.makefile.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.path.FileReference;
import consulo.language.psi.path.FileReferenceSet;

public class MakefileFilenameMixin extends ASTWrapperPsiElement {
    MakefileFilenameMixin(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public FileReference[] getReferences() {
        return new FileReferenceSet(getNode().getPsi()).getAllReferences();
    }
}
