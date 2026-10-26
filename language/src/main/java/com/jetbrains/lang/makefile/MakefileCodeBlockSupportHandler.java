package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.MakefileFoldingBuilder.MakefileConditionalElseFoldingDescriptor;
import com.jetbrains.lang.makefile.MakefileFoldingBuilder.MakefileConditionalFoldingDescriptor;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.editor.highlight.AbstractCodeBlockSupportHandler;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.CONDITIONAL;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.CONDITIONAL_ELSE;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_ELSE;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_ENDIF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFDEF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFEQ;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFNDEF;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.KEYWORD_IFNEQ;

/**
 * Code block handler (which highlights matching tokens) for constructs like
 *
 * <pre>
 * ifdef ...
 *   ...
 * else ifeq ...
 *   ...
 * endif
 * </pre>
 * <p>
 * Pairs of {@code define ... endef} are handled (in a simpler way) via
 * {@link MakefileBraceMatcherProvider}.
 * <p>
 * Block Folding is handled via {@link MakefileConditionalFoldingDescriptor} and
 * {@link MakefileConditionalElseFoldingDescriptor}.
 *
 * @see MakefileBraceMatcherProvider
 * @see MakefileConditionalFoldingDescriptor
 * @see MakefileConditionalElseFoldingDescriptor
 */
@ExtensionImpl
public class MakefileCodeBlockSupportHandler extends AbstractCodeBlockSupportHandler {
    private static final Map<IElementType, TokenSet> HIERARCHY_MAP = new LinkedHashMap<>();

    private static final TokenSet TOP_LEVEL_ELEMENTS;

    private static final TokenSet KEYWORD_ELEMENTS;

    private static final TokenSet BLOCK_ELEMENTS;

    static {
        HIERARCHY_MAP.put(CONDITIONAL, TokenSet.create(KEYWORD_IFDEF,
            KEYWORD_IFNDEF,
            KEYWORD_IFEQ,
            KEYWORD_IFNEQ,
            CONDITIONAL_ELSE,
            KEYWORD_ENDIF));
        HIERARCHY_MAP.put(CONDITIONAL_ELSE, TokenSet.create(KEYWORD_ELSE,
            KEYWORD_IFDEF,
            KEYWORD_IFNDEF,
            KEYWORD_IFEQ,
            KEYWORD_IFNEQ));

        Set<IElementType> parents = new LinkedHashSet<>();
        Set<IElementType> children = new LinkedHashSet<>();

        HIERARCHY_MAP.forEach((parent, immediateChildren) -> {
            parents.add(parent);
            children.addAll(Arrays.asList(immediateChildren.getTypes()));
        });

        Set<IElementType> topLevel = new LinkedHashSet<>(parents);
        topLevel.removeAll(children);
        TOP_LEVEL_ELEMENTS = TokenSet.create(topLevel.toArray(IElementType[]::new));

        Set<IElementType> keywords = new LinkedHashSet<>(children);
        keywords.removeAll(parents);
        KEYWORD_ELEMENTS = TokenSet.create(keywords.toArray(IElementType[]::new));

        BLOCK_ELEMENTS = TokenSet.create(parents.toArray(IElementType[]::new));
    }

    @Override
    protected TokenSet getTopLevelElementTypes() {
        return TOP_LEVEL_ELEMENTS;
    }

    @Override
    protected TokenSet getKeywordElementTypes() {
        return KEYWORD_ELEMENTS;
    }

    @Override
    protected TokenSet getBlockElementTypes() {
        return BLOCK_ELEMENTS;
    }

    @Override
    protected TokenSet getDirectChildrenElementTypes(@Nullable IElementType parentElementType) {
        if (parentElementType == null) {
            return TokenSet.EMPTY;
        }
        TokenSet children = HIERARCHY_MAP.get(parentElementType);
        return children != null ? children : TokenSet.EMPTY;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
