package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTypes;
import com.jetbrains.lang.makefile.psi.impl.MakefileTypesFactory;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;

@ExtensionImpl
public class MakefileParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new MakefileStubFileElementType();

    private static class TokenSets {
        static final TokenSet WHITE_SPACES = TokenSet.WHITE_SPACE;
        static final TokenSet COMMENTS = TokenSet.create(MakefileTypes.COMMENT, MakefileTypes.DOC_COMMENT);
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
        return TokenSets.WHITE_SPACES;
    }

    @Override
    public TokenSet getCommentTokens(LanguageVersion languageVersion) {
        return TokenSets.COMMENTS;
    }

    @Override
    public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
        return TokenSet.EMPTY;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MakefileFile(viewProvider);
    }

    @Override
    public PsiParser createParser(LanguageVersion languageVersion) {
        return new MakefileParser();
    }

    @Override
    public Lexer createLexer(LanguageVersion languageVersion) {
        return new MakefileLexerAdapter();
    }

    @Override
    public PsiElement createElement(ASTNode node) {
        return MakefileTypesFactory.createElement(node);
    }
}
