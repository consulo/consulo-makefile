package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTypes;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.BracePair;
import consulo.language.Language;
import consulo.language.PairedBraceMatcher;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiFile;
import org.jspecify.annotations.Nullable;

/**
 * Provides brace matching support for:
 * <ul>
 *  <li>{@code define ... endef};</li>
 *  <li>{@code { ... }};</li>
 *  <li>{@code ( ... )}.</li>
 * </ul>
 * More complex cases, like {@code ifdef/ifndef/ifeq/ifneq ... else ... endif} are
 * handled via {@link MakefileCodeBlockSupportHandler}.
 *
 * @see MakefileCodeBlockSupportHandler
 */
@ExtensionImpl
public class MakefileBraceMatcherProvider implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = {
        new BracePair(MakefileTypes.KEYWORD_DEFINE, MakefileTypes.KEYWORD_ENDEF, true),
        new BracePair(MakefileTypes.OPEN_CURLY, MakefileTypes.CLOSE_CURLY, true),
        new BracePair(MakefileTypes.OPEN_PAREN, MakefileTypes.CLOSE_PAREN, true)
    };

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }

    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
