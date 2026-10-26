package com.jetbrains.lang.makefile.psi;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.LiteralTextEscaper;
import consulo.language.psi.PsiLanguageInjectionHost;

public abstract class MakefileSubstitutionInjectionHost extends ASTWrapperPsiElement implements MakefileSubstitution {
    public MakefileSubstitutionInjectionHost(ASTNode node) {
        super(node);
    }

    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new MakefileSubstitutionTextEscaper(this);
    }

    @Override
    public MakefileSubstitution updateText(String text) {
        MakefileSubstitution command = MakefileElementFactory.createSubstitution(getProject(), text);
        return (MakefileSubstitution) this.replace(command);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }
}
