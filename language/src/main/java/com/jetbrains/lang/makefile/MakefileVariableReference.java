package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTypes;
import com.jetbrains.lang.makefile.psi.MakefileVariable;
import com.jetbrains.lang.makefile.psi.MakefileVariableUsage;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementResolveResult;
import consulo.language.psi.PsiPolyVariantReferenceBase;
import consulo.language.psi.ResolveResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class MakefileVariableReference extends PsiPolyVariantReferenceBase<MakefileVariableUsage> {
    private final MakefileVariableUsage myUsage;

    public MakefileVariableReference(MakefileVariableUsage usage) {
        super(usage, false);
        myUsage = usage;
    }

    @Override
    public TextRange getRangeInElement() {
        ASTNode nameNode = getNameNode();
        int startOffset = nameNode.getStartOffset() - myUsage.getNode().getStartOffset();
        return TextRange.create(startOffset, startOffset + nameNode.getTextLength());
    }

    private ASTNode getNameNode() {
        ASTNode second = myUsage.getFirstChild().getNextSibling().getNode();
        if (second.getElementType() == MakefileTypes.OPEN_CURLY || second.getElementType() == MakefileTypes.OPEN_PAREN) {
            return myUsage.getFirstChild().getNextSibling().getNextSibling().getNode();
        }
        else {
            return second;
        }
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof MakefileVariable) {
            return element.getText().equals(getNameNode().getText());
        }
        return false;
    }

    @Override
    public Object[] getVariants() {
        Map<String, MakefileVariable> distinct = new LinkedHashMap<>();
        for (MakefileVariable it : ((MakefileFile) myUsage.getContainingFile()).getVariables()) {
            distinct.putIfAbsent(it.getText(), it);
        }
        return distinct.values().stream().map(LookupElementBuilder::create).toArray();
    }

    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        String name = getNameNode().getText();
        return ((MakefileFile) myUsage.getContainingFile()).getVariables().stream()
            .filter(it -> it.getText().equals(name))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }
}
