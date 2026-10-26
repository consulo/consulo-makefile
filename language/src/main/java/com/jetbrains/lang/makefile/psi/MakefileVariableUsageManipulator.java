package com.jetbrains.lang.makefile.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.psi.AbstractElementManipulator;

@ExtensionImpl
public class MakefileVariableUsageManipulator extends AbstractElementManipulator<MakefileVariableUsage> {
    @Override
    public MakefileVariableUsage handleContentChange(MakefileVariableUsage element, TextRange range, String newContent) {
        ASTNode newNameNode = MakefileElementFactory.createChars(element.getProject(), newContent);

        ASTNode second = element.getFirstChild().getNextSibling().getNode();
        ASTNode nameNode;
        if (second.getElementType() == MakefileTypes.OPEN_CURLY || second.getElementType() == MakefileTypes.OPEN_PAREN) {
            nameNode = element.getFirstChild().getNextSibling().getNextSibling().getNode();
        }
        else {
            nameNode = second;
        }

        element.getNode().replaceChild(nameNode, newNameNode);
        return element;
    }

    @Override
    public Class<MakefileVariableUsage> getElementClass() {
        return MakefileVariableUsage.class;
    }
}
