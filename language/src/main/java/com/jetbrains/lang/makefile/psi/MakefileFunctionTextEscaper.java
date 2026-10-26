package com.jetbrains.lang.makefile.psi;

import consulo.document.util.TextRange;
import consulo.language.psi.LiteralTextEscaper;

import java.util.regex.Pattern;

public class MakefileFunctionTextEscaper extends LiteralTextEscaper<MakefileFunction> {
    private static final Pattern DOUBLE_DOLLAR = Pattern.compile(Pattern.quote("$$"));

    private final MakefileFunction myFunction;

    public MakefileFunctionTextEscaper(MakefileFunction function) {
        super(function);
        myFunction = function;
    }

    @Override
    public boolean isOneLine() {
        return !myFunction.textContains('\n');
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, TextRange rangeInsideHost) {
        int shift = 0;
        int i = 0;
        for (String part : DOUBLE_DOLLAR.split(rangeInsideHost.substring(myFunction.getText()), -1)) {
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
            outChars.append(rangeInsideHost.substring(myFunction.getText()).replace("$$", "$"));
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }
}
