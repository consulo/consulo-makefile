package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileComment;
import com.jetbrains.lang.makefile.psi.MakefileDocComment;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.language.spellcheker.SpellcheckingStrategy;
import consulo.language.spellcheker.tokenizer.TokenConsumer;
import consulo.language.spellcheker.tokenizer.Tokenizer;
import consulo.language.spellcheker.tokenizer.splitter.PlainTextTokenSplitter;

@ExtensionImpl
public class MakefileSpellcheckingStrategy extends SpellcheckingStrategy implements DumbAware {
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if (element instanceof MakefileComment) {
            return MakefileCommentTokenizer.INSTANCE;
        }
        if (element instanceof MakefileDocComment) {
            return MakefileDocCommentTokenizer.INSTANCE;
        }
        return super.getTokenizer(element);
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }

    static class MakefileCommentTokenizer extends Tokenizer<MakefileComment> {
        static final MakefileCommentTokenizer INSTANCE = new MakefileCommentTokenizer();

        @Override
        public void tokenize(MakefileComment element, TokenConsumer consumer) {
            consumer.consumeToken(element, PlainTextTokenSplitter.getInstance());
        }
    }

    static class MakefileDocCommentTokenizer extends Tokenizer<MakefileDocComment> {
        static final MakefileDocCommentTokenizer INSTANCE = new MakefileDocCommentTokenizer();

        @Override
        public void tokenize(MakefileDocComment element, TokenConsumer consumer) {
            consumer.consumeToken(element, PlainTextTokenSplitter.getInstance());
        }
    }
}
