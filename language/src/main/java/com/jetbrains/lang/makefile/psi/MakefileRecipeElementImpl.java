package com.jetbrains.lang.makefile.psi;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.LiteralTextEscaper;
import consulo.language.psi.PsiLanguageInjectionHost;

public abstract class MakefileRecipeElementImpl extends ASTWrapperPsiElement implements MakefileRecipe {
    public MakefileRecipeElementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new MakefileRecipeTextEscaper(this);
    }

    @Override
    public MakefileRecipe updateText(String text) {
        MakefileRecipe command = MakefileElementFactory.createRecipe(getProject(), text);
        return (MakefileRecipe) this.replace(command);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }
}
