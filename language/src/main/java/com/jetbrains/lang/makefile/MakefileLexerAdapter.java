package com.jetbrains.lang.makefile;

import consulo.language.lexer.FlexAdapter;

public class MakefileLexerAdapter extends FlexAdapter {
    public MakefileLexerAdapter() {
        super(new _MakefileLexer());
    }
}
