package com.jetbrains.lang.makefile.psi.impl;

import com.jetbrains.lang.makefile.psi.MakefileNamedElement;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;

public abstract class MakefileVariableNamedElementImpl extends ASTWrapperPsiElement implements MakefileNamedElement {
    private final ASTNode myNode;

    public MakefileVariableNamedElementImpl(ASTNode node) {
        super(node);
        myNode = node;
    }

    @Override
    public String toString() {
        return "MakefileVariableImpl(VARIABLE)";
    }

    @Override
    public ASTNode getNode() {
        return myNode;
    }
}
