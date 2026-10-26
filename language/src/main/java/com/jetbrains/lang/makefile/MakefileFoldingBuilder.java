package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileCondition;
import com.jetbrains.lang.makefile.psi.MakefileConditional;
import com.jetbrains.lang.makefile.psi.MakefileConditionalElse;
import com.jetbrains.lang.makefile.psi.MakefileDefine;
import com.jetbrains.lang.makefile.psi.MakefileBlock;
import com.jetbrains.lang.makefile.psi.MakefileRule;
import com.jetbrains.lang.makefile.psi.MakefileVariable;
import com.jetbrains.lang.makefile.psi.MakefileVariableAssignment;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.document.Document;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.editor.folding.FoldingBuilderEx;
import consulo.language.editor.folding.FoldingDescriptor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.psi.util.PsiTreeUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_ENDIF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFDEF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFEQ;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFNDEF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFNEQ;

@ExtensionImpl
public class MakefileFoldingBuilder extends FoldingBuilderEx implements DumbAware {
    private static final String DEFAULT_PLACEHOLDER_TEXT = "...";

    @Override
    @SuppressWarnings("unchecked")
    public FoldingDescriptor[] buildFoldRegions(PsiElement root, Document document, boolean quick) {
        if (!(root instanceof MakefileFile)) {
            return FoldingDescriptor.EMPTY;
        }

        List<FoldingDescriptor> result = new ArrayList<>();
        for (PsiElement it : PsiTreeUtil.<PsiElement>findChildrenOfAnyType(root,
            MakefileRule.class,
            MakefileVariableAssignment.class,
            MakefileDefine.class,
            MakefileConditional.class,
            MakefileConditionalElse.class)) {
            FoldingDescriptor descriptor = null;
            if (it instanceof MakefileRule rule) {
                descriptor = new MakefileRuleFoldingDescriptor(rule);
            }
            else if (it instanceof MakefileVariableAssignment variable) {
                descriptor = new MakefileVariableFoldingDescriptor(variable);
            }
            else if (it instanceof MakefileDefine define) {
                descriptor = new MakefileDefineFoldingDescriptor(define);
            }
            else if (it instanceof MakefileConditional conditional) {
                TextRange range = withoutFirstNode(conditional);
                descriptor = !range.isEmpty() ? new MakefileConditionalFoldingDescriptor(conditional, range) : null;
            }
            else if (it instanceof MakefileConditionalElse conditionalElse) {
                /*-
                 * A conditional else may be followed by either:
                 *
                 * - the next conditional else, or
                 * - the `endif` keyword (should be folded along with the block).
                 */
                TextRange range = withoutFirstNodeWithNextSiblingOfType(conditionalElse, KEYWORD_ENDIF);
                descriptor = range.isEmpty() ? null : new MakefileConditionalElseFoldingDescriptor(conditionalElse, range);
            }
            if (descriptor != null) {
                result.add(descriptor);
            }
        }
        return result.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public String getPlaceholderText(ASTNode node) {
        return DEFAULT_PLACEHOLDER_TEXT;
    }

    @Override
    public boolean isCollapsedByDefault(ASTNode node) {
        return node.getPsi() instanceof MakefileDefine;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }

    public static String cutValue(@Nullable String value) {
        if (value == null) {
            return "";
        }
        String cut = value.length() > 60 ? value.substring(0, 42) + DEFAULT_PLACEHOLDER_TEXT : value;
        return cut.trim();
    }

    public static TextRange trimmedTextRange(PsiElement element) {
        String text = element.getText();
        int lastNonWhitespace = -1;
        for (int i = text.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(text.charAt(i))) {
                lastNonWhitespace = i;
                break;
            }
        }
        return TextRange.create(element.getTextRange().getStartOffset(), element.getTextRange().getStartOffset() + lastNonWhitespace + 1);
    }

    /**
     * @see #withoutFirstNodeWithNextSiblingOfType
     */
    public static TextRange withoutFirstNode(PsiElement element) {
        PsiElement firstChild = element.getFirstChild();
        PsiElement next = firstChild != null ? nextNonWhiteSpaceSibling(firstChild) : null;
        int startOffset = next != null ? next.getTextRange().getStartOffset() : element.getTextRange().getEndOffset();
        return TextRange.create(startOffset, element.getTextRange().getEndOffset());
    }

    /**
     * @param siblingType the expected type of the next sibling. If it matches
     *                    the actual type of the next sibling, the sibling is included in the
     *                    range returned.
     * @see #withoutFirstNode
     */
    public static TextRange withoutFirstNodeWithNextSiblingOfType(PsiElement element, IElementType siblingType) {
        PsiElement nextSibling = nextNonWhiteSpaceSibling(element);

        if (nextSibling == null) {
            return withoutFirstNode(element);
        }

        if (nextSibling.getNode().getElementType() == siblingType) {
            /*
             * Conditional else:
             *
             * - the 1st child is always `else`;
             * - the 2nd child may be a COMMENT, en EOL, or any of ifdef/ifndef/ifeq/ifneq.
             */
            PsiElement firstChild = element.getFirstChild();
            PsiElement next = firstChild != null ? nextNonWhiteSpaceSibling(firstChild) : null;
            int startOffset = next != null ? next.getTextRange().getStartOffset() : element.getTextRange().getEndOffset();

            /*
             * Include the next sibling in the range returned.
             */
            int endOffset = nextSibling.getTextRange().getEndOffset();
            return TextRange.create(startOffset, endOffset);
        }

        return withoutFirstNode(element);
    }

    private static @Nullable PsiElement nextNonWhiteSpaceSibling(PsiElement element) {
        PsiElement nextSibling = element.getNextSibling();
        while (nextSibling instanceof PsiWhiteSpace) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling;
    }

    public static class MakefileRuleFoldingDescriptor extends FoldingDescriptor {
        private final MakefileRule myRule;

        public MakefileRuleFoldingDescriptor(MakefileRule rule) {
            super(rule, trimmedTextRange(rule));
            myRule = rule;
        }

        @Override
        public String getPlaceholderText() {
            return myRule.getTargetLine().getTargets().getText() + ":";
        }
    }

    public static class MakefileVariableFoldingDescriptor extends FoldingDescriptor {
        private final MakefileVariableAssignment myVariable;

        public MakefileVariableFoldingDescriptor(MakefileVariableAssignment variable) {
            super(variable, trimmedTextRange(variable));
            myVariable = variable;
        }

        @Override
        public String getPlaceholderText() {
            PsiElement assignment = myVariable.getAssignment();
            return myVariable.getVariable().getText() + (assignment != null ? assignment.getText() : "=") + cutValue(myVariable.getValue());
        }
    }

    public static class MakefileDefineFoldingDescriptor extends FoldingDescriptor {
        private final MakefileDefine myDefine;

        public MakefileDefineFoldingDescriptor(MakefileDefine define) {
            super(define, trimmedTextRange(define));
            myDefine = define;
        }

        @Override
        public String getPlaceholderText() {
            MakefileVariable variable = myDefine.getVariable();
            PsiElement assignment = myDefine.getAssignment();
            return (variable != null ? variable.getText() : null) + (assignment != null ? assignment.getText() : "=") + cutValue(myDefine.getValue());
        }
    }

    /**
     * Handles the folding of {@code ifeq/ifneq/ifdef/ifndef ... endif} pairs.
     *
     * @see MakefileConditionalElseFoldingDescriptor
     * @see MakefileCodeBlockSupportHandler
     */
    public static class MakefileConditionalFoldingDescriptor extends FoldingDescriptor {
        private final MakefileConditional myConditional;

        public MakefileConditionalFoldingDescriptor(MakefileConditional conditional, TextRange range) {
            super(conditional, range);
            myConditional = conditional;
        }

        @Override
        public String getPlaceholderText() {
            MakefileCondition condition = myConditional.getCondition();
            return cutValue(condition != null ? condition.getText() : null);
        }
    }

    /**
     * Handles the folding of {@code else ... endif} pairs.
     *
     * @see MakefileConditionalFoldingDescriptor
     * @see MakefileCodeBlockSupportHandler
     */
    public static class MakefileConditionalElseFoldingDescriptor extends FoldingDescriptor {
        private static final Set<IElementType> CONDITIONAL_KEYWORDS = Set.of(KEYWORD_IFDEF, KEYWORD_IFNDEF, KEYWORD_IFEQ, KEYWORD_IFNEQ);

        private final MakefileConditionalElse myConditionalElse;

        public MakefileConditionalElseFoldingDescriptor(MakefileConditionalElse conditionalElse, TextRange range) {
            super(conditionalElse, range);
            myConditionalElse = conditionalElse;
        }

        /**
         * @return the short value of either the condition (if available), or the
         * enclosed block (otherwise).
         */
        @Override
        public String getPlaceholderText() {
            MakefileCondition conditionElement = myConditionalElse.getCondition();
            String condition = conditionElement != null ? conditionElement.getText() : null;
            /*
             * else <block> endif
             */
            if (condition == null) {
                MakefileBlock block = myConditionalElse.getBlock();
                String placeholderText = cutValue(block != null ? block.getText() : null);
                /*
                 * The body of the `else` clause consists of whitespace and/or
                 * comments, return "else ..." in this case.
                 */
                if (placeholderText.isEmpty()) {
                    return " " + DEFAULT_PLACEHOLDER_TEXT;
                }

                /*
                 * Return "else <descriptor>" instead of "else<descriptor>".
                 */
                return " " + placeholderText;
            }

            /*
             * else (ifdef|ifndef|ifeq|ifneq) <condition> <block> endif
             */

            /*
             * The 1st child is always `else`.
             * The 2nd child is either of ifdef/ifndef/ifeq/ifneq.
             */
            PsiElement firstChild = myConditionalElse.getFirstChild();
            PsiElement secondChild = firstChild != null ? nextNonWhiteSpaceSibling(firstChild) : null;

            /*
             * Use "(ifdef|ifndef|ifeq|ifneq) <condition>" and abbreviate it.
             */
            String placeholderText;
            if (secondChild != null && CONDITIONAL_KEYWORDS.contains(secondChild.getNode().getElementType())) {
                placeholderText = cutValue(secondChild.getText() + ' ' + condition);
            }
            else {
                /*
                 * Unlikely.
                 */
                placeholderText = cutValue(condition);
            }

            /*-
             * Since a conditional else may be followed by another conditional else,
             * append a space to the end of the descriptor.
             *
             * In other words, return
             *
             * "else <descriptor> else ... endif"
             *
             * instead of
             *
             * "else <descriptor>else ... endif".
             */
            return placeholderText.endsWith(" ") ? placeholderText : placeholderText + " ";
        }
    }
}
