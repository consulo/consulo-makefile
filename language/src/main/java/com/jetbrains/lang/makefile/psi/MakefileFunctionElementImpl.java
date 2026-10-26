package com.jetbrains.lang.makefile.psi;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.LiteralTextEscaper;
import consulo.language.psi.PsiLanguageInjectionHost;

public abstract class MakefileFunctionElementImpl extends ASTWrapperPsiElement implements MakefileFunction {
    public MakefileFunctionElementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new MakefileFunctionTextEscaper(this);
    }

    @Override
    public MakefileFunction updateText(String text) {
        MakefileFunction fn = MakefileElementFactory.createFunction(getProject(), text);
        return (MakefileFunction) this.replace(fn);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }
}
