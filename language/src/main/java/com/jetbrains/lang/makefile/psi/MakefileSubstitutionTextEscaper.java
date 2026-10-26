package com.jetbrains.lang.makefile.psi;

import consulo.document.util.TextRange;
import consulo.language.psi.LiteralTextEscaper;

import java.util.regex.Pattern;

public class MakefileSubstitutionTextEscaper extends LiteralTextEscaper<MakefileSubstitution> {
    private static final Pattern DOUBLE_DOLLAR = Pattern.compile(Pattern.quote("$$"));

    private final MakefileSubstitution mySubstitution;

    public MakefileSubstitutionTextEscaper(MakefileSubstitution substitution) {
        super(substitution);
        mySubstitution = substitution;
    }

    @Override
    public boolean isOneLine() {
        return !mySubstitution.textContains('\n');
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, TextRange rangeInsideHost) {
        int shift = 0;
        int i = 0;
        for (String part : DOUBLE_DOLLAR.split(rangeInsideHost.substring(mySubstitution.getText()), -1)) {
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
            outChars.append(rangeInsideHost.substring(mySubstitution.getText()).replace("$$", "$"));
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }
}
