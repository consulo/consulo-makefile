package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import consulo.language.util.ProcessingContext;

import java.util.List;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.DOLLAR;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.EOL;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.OPEN_PAREN;
import static consulo.language.pattern.PlatformPatterns.psiComment;
import static consulo.language.pattern.PlatformPatterns.psiElement;

@ExtensionImpl
public class MakefileCompletionContributor extends CompletionContributor {
    private static class KeywordCompletionProvider implements CompletionProvider {
        private static final List<String> KEYWORDS =
            List.of("include", "define", "undefine", "override", "export", "private", "vpath", "ifeq", "ifneq", "ifdef", "ifndef");

        @Override
        public void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultSet) {
            resultSet.addAllElements(KEYWORDS.stream().map(LookupElementBuilder::create).toList());
        }
    }

    private static class FunctionCompletionProvider implements CompletionProvider {
        private static final List<String> FUNCTIONS = List.of("error", "warning", "info", "shell", "subst", "patsubst", "strip", "findstring",
            "filter", "filter-out", "sort", "word", "wordlist", "words", "firstword", "lastword", "dir", "notdir", "suffix",
            "basename", "addsuffix", "addprefix", "join", "wildcard", "realpath", "abspath", "if", "or", "and",
            "foreach", "file", "call", "value", "eval", "origin", "flavor", "guile");

        @Override
        public void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultSet) {
            resultSet.addAllElements(FUNCTIONS.stream().map(LookupElementBuilder::create).toList());
        }
    }

    public MakefileCompletionContributor() {
        extend(
            CompletionType.BASIC,
            StandardPatterns.or(
                psiElement().afterLeaf(psiElement(EOL)).andNot(psiComment()),
                psiElement().isNull()
            ),
            new KeywordCompletionProvider()
        );

        extend(
            CompletionType.BASIC,
            psiElement()
                .afterLeaf(psiElement(OPEN_PAREN).afterLeafSkipping(psiElement(OPEN_PAREN), psiElement(DOLLAR)))
                .andNot(psiComment()),
            new FunctionCompletionProvider()
        );
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
