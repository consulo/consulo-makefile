package com.jetbrains.lang.makefile.psi;

import consulo.document.util.TextRange;
import consulo.language.psi.LiteralTextEscaper;

import java.util.regex.Pattern;

public class MakefileRecipeTextEscaper extends LiteralTextEscaper<MakefileRecipe> {
    private static final Pattern DOUBLE_DOLLAR = Pattern.compile(Pattern.quote("$$"));

    private final MakefileRecipe myRecipe;

    public MakefileRecipeTextEscaper(MakefileRecipe recipe) {
        super(recipe);
        myRecipe = recipe;
    }

    @Override
    public boolean isOneLine() {
        return !myRecipe.textContains('\n');
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, TextRange rangeInsideHost) {
        int shift = 0;
        int i = 0;
        for (String part : DOUBLE_DOLLAR.split(rangeInsideHost.substring(myRecipe.getText()), -1)) {
            if (i + part.length() >= offsetInDecoded) {
                break;
            }
            shift++;
            i += part.length() + 1;
        }
        return rangeInsideHost.getStartOffset() + offsetInDecoded + shift;
    }

    @Override
    public boolean decode(TextRange rangeInsideHost, StringBuilder outChars) {
        try {
            outChars.append(rangeInsideHost.substring(myRecipe.getText()).replace("$$", "$"));
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }
}
