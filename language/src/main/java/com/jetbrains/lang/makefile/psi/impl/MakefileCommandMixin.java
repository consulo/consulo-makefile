package com.jetbrains.lang.makefile.psi.impl;

import com.jetbrains.lang.makefile.psi.MakefileTypes;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;

public abstract class MakefileCommandMixin extends ASTWrapperPsiElement implements IMakeFileCommand {
    public MakefileCommandMixin(ASTNode node) {
        super(node);
    }

    @Override
    public TextRange getShellCommandRange() {
        assert getNode().getFirstChildNode().getElementType() == MakefileTypes.RECIPE_PREFIX;
        assert getNode().getFirstChildNode().getTextLength() == 1;

        String text = getText();
        int textLength = getTextLength();
        int startOffset = 1;
        while (startOffset < textLength) {
            char c = text.charAt(startOffset);
            if (!Character.isWhitespace(c) && c != '@' && c != '-') {
                break;
            }

            startOffset++;
        }

        return getTextRangeInParent().cutOut(TextRange.create(startOffset, textLength));
    }
}
