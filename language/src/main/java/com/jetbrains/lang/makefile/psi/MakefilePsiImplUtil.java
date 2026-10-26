package com.jetbrains.lang.makefile.psi;

import com.jetbrains.lang.makefile.MakefileFile;
import com.jetbrains.lang.makefile.psi.impl.MakefilePrerequisiteImpl;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.navigation.ItemPresentation;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.*;

public final class MakefilePsiImplUtil {
    private static final Pattern SUFFIX_RULE = Pattern.compile("^\\.[a-zA-Z]+(\\.[a-zA-Z]+)$");

    private static final TokenSet ASSIGNMENT = TokenSet.create(ASSIGN);
    private static final TokenSet LINE = TokenSet.create(IDENTIFIER, VARIABLE_USAGE);

    private MakefilePsiImplUtil() {
    }

    public static List<MakefileTarget> getTargets(MakefileRule element) {
        return element.getTargetLine().getTargets().getTargetList();
    }

    public static @Nullable String getTargetName(MakefileTargetLine element) {
        ASTNode targetNode = element.getNode().findChildByType(TARGET);
        if (targetNode == null) {
            return null;
        }
        return targetNode.getText();
    }

    public static String getName(MakefileTarget element) {
        return element.getText();
    }

    public static PsiElement setName(MakefileTarget element, String newName) {
        ASTNode identifierNode = element.getNode().getFirstChildNode();
        if (identifierNode != null) {
            MakefileTarget target = MakefileElementFactory.createTarget(element.getProject(), newName);
            ASTNode newIdentifierNode = target.getFirstChild().getNode();
            element.getNode().replaceChild(identifierNode, newIdentifierNode);
        }
        return element;
    }

    public static String getName(MakefileVariable element) {
        return element.getText();
    }

    public static PsiElement setName(MakefileVariable element, String newName) {
        ASTNode identifierNode = element.getNode().getFirstChildNode();
        if (identifierNode != null) {
            MakefileVariable variable = MakefileElementFactory.createVariable(element.getProject(), newName);
            ASTNode newIdentifierNode = variable.getFirstChild().getNode();
            element.getNode().replaceChild(identifierNode, newIdentifierNode);
        }
        return element;
    }

    public static @Nullable PsiElement getNameIdentifier(MakefileTarget element) {
        if (element.isSpecialTarget()) {
            return null;
        }

        ASTNode targetNode = element.getNode();
        return targetNode != null ? targetNode.getPsi() : null;
    }

    public static @Nullable PsiElement getNameIdentifier(MakefileVariable element) {
        return element.getFirstChild();
    }

    public static @Nullable String getDocComment(MakefileTarget element) {
        if (element.isSpecialTarget()) {
            return null;
        }

        MakefileTargetLine targetLine = (MakefileTargetLine) element.getParent().getParent();

        Collection<PsiComment> comments = PsiTreeUtil.findChildrenOfType(targetLine, PsiComment.class);
        for (PsiComment comment : comments) {
            if (comment.getTokenType() == DOC_COMMENT) {
                return comment.getText().substring(2);
            }
        }

        return null;
    }

    public static ItemPresentation getPresentation(MakefileTarget element) {
        return new MakefileTargetPresentation(element);
    }

    public static boolean isSpecialTarget(MakefileTarget element) {
        String name = element.getName();
        return name.matches("^\\.[A-Z_]*") || name.equals("FORCE") || SUFFIX_RULE.matcher(name).matches();
    }

    public static boolean isPatternTarget(MakefileTarget element) {
        return element.getName().contains("%");
    }

    public static boolean matches(MakefileTarget element, String prerequisite) {
        String name = element.getName();
        if (name.startsWith("%")) {
            return prerequisite.endsWith(name.substring(1));
        }
        if (name.endsWith("%")) {
            return prerequisite.startsWith(name.substring(0, name.length() - 1));
        }
        Matcher matcher = SUFFIX_RULE.matcher(name);
        if (matcher.matches()) {
            return prerequisite.endsWith(matcher.group(1));
        }
        return name.equals(prerequisite);
    }

    public static @Nullable PsiElement getAssignment(MakefileVariableAssignment element) {
        ASTNode node = element.getNode().findChildByType(ASSIGNMENT);
        if (node == null) {
            return null;
        }
        return node.getPsi();
    }

    public static String getValue(MakefileVariableAssignment element) {
        MakefileVariableValue value = element.getVariableValue();
        if (value == null) {
            return "";
        }
        ASTNode[] nodes = value.getNode().getChildren(TokenSet.ANY);
        return Arrays.stream(nodes).map(ASTNode::getText).collect(Collectors.joining("\n"));
    }

    public static @Nullable PsiElement getAssignment(MakefileDefine element) {
        ASTNode node = element.getNode().findChildByType(ASSIGNMENT);
        if (node == null) {
            return null;
        }
        return node.getPsi();
    }

    public static String getValue(MakefileDefine element) {
        ASTNode[] nodes = element.getNode().getChildren(TokenSet.ANY);
        return Arrays.stream(nodes)
            .dropWhile(it -> it.getElementType() != EOL)
            .filter(it -> it.getElementType() != KEYWORD_ENDEF)
            .map(ASTNode::getText)
            .collect(Collectors.joining("\n"));
    }

    public static boolean isEmpty(MakefileRecipe element) {
        return element.getCommandList().isEmpty() && element.getConditionalList().isEmpty();
    }

    public static MakefilePrerequisiteImpl updateText(MakefilePrerequisite prerequisite, String newText) {
        MakefilePrerequisite replacement = MakefileElementFactory.createPrerequisite(prerequisite.getProject(), newText);
        return (MakefilePrerequisiteImpl) prerequisite.replace(replacement);
    }

    public static boolean isPhonyTarget(MakefilePrerequisite prerequisite) {
        if (prerequisite.getContainingFile() instanceof MakefileFile file) {
            for (MakefileRule rule : file.getPhonyRules()) {
                MakefilePrerequisites prerequisites = rule.getTargetLine().getPrerequisites();
                if (prerequisites != null) {
                    MakefileNormalPrerequisites normalPrerequisites = prerequisites.getNormalPrerequisites();
                    for (MakefilePrerequisite goal : normalPrerequisites.getPrerequisiteList()) {
                        if (goal.getText().equals(prerequisite.getText())) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }
}
