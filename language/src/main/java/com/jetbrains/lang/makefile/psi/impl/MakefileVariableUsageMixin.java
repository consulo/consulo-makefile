package com.jetbrains.lang.makefile.psi.impl;

import com.jetbrains.lang.makefile.MakefileVariableReference;
import com.jetbrains.lang.makefile.psi.MakefileVariableUsage;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiReference;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.util.lang.lazy.LazyValue;

import java.util.function.Supplier;

public abstract class MakefileVariableUsageMixin extends ASTWrapperPsiElement implements MakefileVariableUsage {
    private final Supplier<PsiReference[]> myReference = LazyValue.notNull(() -> new PsiReference[]{new MakefileVariableReference(this)});

    MakefileVariableUsageMixin(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference[] getReferences() {
        return myReference.get();
    }
}
