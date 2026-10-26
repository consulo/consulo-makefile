package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileFunction;
import com.jetbrains.lang.makefile.psi.MakefileFunctionName;
import com.jetbrains.lang.makefile.psi.MakefilePrerequisite;
import com.jetbrains.lang.makefile.psi.MakefilePrerequisites;
import com.jetbrains.lang.makefile.psi.MakefileRecipe;
import com.jetbrains.lang.makefile.psi.MakefileRule;
import com.jetbrains.lang.makefile.psi.MakefileString;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileTargetLine;
import com.jetbrains.lang.makefile.psi.MakefileTypes;
import com.jetbrains.lang.makefile.psi.MakefileVariable;
import com.jetbrains.lang.makefile.psi.MakefileVariableUsage;
import com.jetbrains.lang.makefile.psi.MakefileVariableValue;
import consulo.application.dumb.DumbAware;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.HighlightSeverity;
import consulo.language.psi.PsiElement;

import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.FUNCTION;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.PREREQUISITE;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.SPECIAL_TARGET;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.STRING;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.TARGET;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.VARIABLE;
import static com.jetbrains.lang.makefile.MakefileSyntaxHighlighter.VARIABLE_VALUE;

public class MakefileAnnotator implements Annotator, DumbAware {
    private final TokenSet myLineTokenSet = TokenSet.create(MakefileTypes.IDENTIFIER);

    @Override
    public void annotate(PsiElement element, AnnotationHolder holder) {
        if (holder.isBatchMode()) {
            return;
        }
        if (element instanceof MakefileTarget target && !isUnused((MakefileRule) element.getParent().getParent().getParent())) {
            mark(holder, element, target.isSpecialTarget() ? SPECIAL_TARGET : TARGET);
        }
        else if (element instanceof MakefilePrerequisite) {
            mark(holder, element, PREREQUISITE);
        }
        else if (element instanceof MakefileVariable) {
            mark(holder, element, VARIABLE);
        }
        else if (element instanceof MakefileVariableValue) {
            for (ASTNode it : element.getNode().getChildren(myLineTokenSet)) {
                mark(holder, it, VARIABLE_VALUE);
            }
        }
        else if (element instanceof MakefileFunctionName && element.getParent() instanceof MakefileFunction) {
            mark(holder, element, FUNCTION);
        }
        else if (element instanceof MakefileString) {
            mark(holder, element, STRING);
        }
        else if (element instanceof MakefileVariableUsage) {
            mark(holder, element, VARIABLE);
        }
    }

    private static void mark(AnnotationHolder holder, PsiElement element, TextAttributesKey attr) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(element).textAttributes(attr).create();
    }

    private static void mark(AnnotationHolder holder, ASTNode node, TextAttributesKey attr) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(node).textAttributes(attr).create();
    }

    private static boolean isUnused(MakefileRule rule) {
        MakefileRecipe recipe = rule.getRecipe();
        if (recipe != null && !recipe.isEmpty()) {
            return false;
        }
        MakefileTargetLine targetLine = rule.getTargetLine();
        if (targetLine.getTargets().getTargetList().stream().anyMatch(it -> it.isSpecialTarget() || it.isPatternTarget())) {
            return false;
        }
        MakefilePrerequisites prerequisites = targetLine.getPrerequisites();
        if (prerequisites != null && !prerequisites.getNormalPrerequisites().getPrerequisiteList().isEmpty()) {
            return false;
        }
        if (prerequisites != null && prerequisites.getOrderOnlyPrerequisites() != null
            && !prerequisites.getOrderOnlyPrerequisites().getPrerequisiteList().isEmpty()) {
            return false;
        }
        if (targetLine.getVariableAssignment() != null) {
            return false;
        }
        if (targetLine.getPrivatevar() != null) {
            return false;
        }

        return true;
    }
}
