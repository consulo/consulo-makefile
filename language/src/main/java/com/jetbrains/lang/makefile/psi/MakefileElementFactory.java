package com.jetbrains.lang.makefile.psi;

import com.jetbrains.lang.makefile.MakefileFile;
import com.jetbrains.lang.makefile.MakefileFileType;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFileFactory;
import consulo.language.psi.PsiWhiteSpace;
import consulo.project.Project;

import java.util.Objects;

public final class MakefileElementFactory {
    private MakefileElementFactory() {
    }

    private static MakefileFile createFile(Project project, String text) {
        return (MakefileFile) PsiFileFactory.getInstance(project).createFileFromText("Makefile", MakefileFileType.INSTANCE, text);
    }

    public static MakefileRule createRule(Project project, String target) {
        return (MakefileRule) createFile(project, target + ":").getFirstChild();
    }

    public static MakefileTarget createTarget(Project project, String name) {
        return (MakefileTarget) createRule(project, name).getFirstChild().getFirstChild().getFirstChild();
    }

    public static MakefileVariable createVariable(Project project, String name) {
        return (MakefileVariable) createFile(project, name + " := ''").getFirstChild().getFirstChild();
    }

    public static MakefilePrerequisite createPrerequisite(Project project, String name) {
        MakefileRule rule = (MakefileRule) createFile(project, "a: " + name).getFirstChild();
        MakefilePrerequisites prerequisites = Objects.requireNonNull(rule.getTargetLine().getPrerequisites());
        return (MakefilePrerequisite) prerequisites.getNormalPrerequisites().getFirstChild();
    }

    public static PsiWhiteSpace createWhiteSpace(Project project, String whitespace) {
        return (PsiWhiteSpace) createFile(project, whitespace).getFirstChild();
    }

    public static ASTNode createChars(Project project, String chars) {
        return createFile(project, chars).getFirstChild().getNode();
    }

    public static PsiElement createEOL(Project project, String whitespace) {
        return createFile(project, whitespace).getFirstChild();
    }

    public static MakefileRecipe createRecipe(Project project, String text) {
        return (MakefileRecipe) createRule(project, "target:\n\t" + text).getFirstChild().getNextSibling();
    }

    public static MakefileFunction createFunction(Project project, String text) {
        return (MakefileFunction) createFile(project, text).getFirstChild();
    }

    public static MakefileSubstitution createSubstitution(Project project, String text) {
        return createRecipe(project, text).getCommandList().get(0).getSubstitutionList().get(0);
    }
}
