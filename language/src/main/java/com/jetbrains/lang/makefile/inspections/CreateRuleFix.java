package com.jetbrains.lang.makefile.inspections;

import com.jetbrains.lang.makefile.MakefileFile;
import com.jetbrains.lang.makefile.psi.MakefileElementFactory;
import consulo.language.ast.ASTNode;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;
import consulo.project.Project;

public class CreateRuleFix implements LocalQuickFix {
    @Override
    public LocalizeValue getName() {
        return MakefileLocalize.intentionNameCreateRule();
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public void applyFix(Project project, ProblemDescriptor descriptor) {
        PsiElement prerequisite = descriptor.getPsiElement();
        if (!(prerequisite.getContainingFile() instanceof MakefileFile file)) {
            return;
        }
        PsiElement rule = MakefileElementFactory.createRule(project, prerequisite.getText());
        PsiElement anchorElement = prerequisite.getParent().getParent().getParent().getParent().getNextSibling();
        ASTNode anchor = anchorElement != null ? anchorElement.getNode() : null;
        file.getNode().addChild(MakefileElementFactory.createEOL(project, "\n").getNode(), anchor);
        file.getNode().addChild(rule.getNode(), anchor);
        file.getNode().addChild(MakefileElementFactory.createEOL(project, "\n").getNode(), anchor);
    }
}
