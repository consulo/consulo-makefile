package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTypes;
import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.codeEditor.HighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;

import static consulo.colorScheme.TextAttributesKey.createTextAttributesKey;

public class MakefileSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey COMMENT = createTextAttributesKey("MAKEFILE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey DOCCOMMENT = createTextAttributesKey("MAKEFILE_DOCCOMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("MAKEFILE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey TARGET = createTextAttributesKey("MAKEFILE_TARGET", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey SPECIAL_TARGET =
        createTextAttributesKey("MAKEFILE_SPECIAL_TARGET", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);
    public static final TextAttributesKey SEPARATOR =
        createTextAttributesKey("MAKEFILE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey PREREQUISITE =
        createTextAttributesKey("MAKEFILE_PREREQUISITE", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey VARIABLE = createTextAttributesKey("MAKEFILE_VARIABLE", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    public static final TextAttributesKey VARIABLE_VALUE = createTextAttributesKey("MAKEFILE_VARIABLE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey STRING = createTextAttributesKey("MAKEFILE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey LINE_SPLIT =
        createTextAttributesKey("MAKEFILE_LINE_SPLIT", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public static final TextAttributesKey TAB = createTextAttributesKey("MAKEFILE_TAB", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey FUNCTION = createTextAttributesKey("MAKEFILE_FUNCTION", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey FUNCTION_PARAM = createTextAttributesKey("MAKEFILE_FUNCTION_PARAM", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BRACES = createTextAttributesKey("MAKEFILE_BRACES", DefaultLanguageHighlighterColors.BRACES);
    private static final TextAttributesKey PARENS = createTextAttributesKey("MAKEFILE_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);

/*
    private fun braces(t: TextAttributesKey) {
      t.foregroundColor = Color(0x00, 0x73, 0xbf)
    }
*/

    private static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("MAKEFILE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] BAD_CHAR_KEYS = {BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = {SEPARATOR};
    private static final TextAttributesKey[] KEYWORD_KEYS = {KEYWORD};
    private static final TextAttributesKey[] TARGET_KEYS = {TARGET};
    private static final TextAttributesKey[] PREREQUISITE_KEYS = {PREREQUISITE};
    private static final TextAttributesKey[] VARIABLE_KEYS = {VARIABLE};
    private static final TextAttributesKey[] VARIABLE_VALUE_KEYS = {VARIABLE_VALUE};
    private static final TextAttributesKey[] STRING_KEYS = {STRING};
    private static final TextAttributesKey[] LINE_SPLIT_KEYS = {LINE_SPLIT};
    private static final TextAttributesKey[] TAB_KEYS = {TAB};
    private static final TextAttributesKey[] COMMENT_KEYS = {COMMENT};
    private static final TextAttributesKey[] DOCCOMMENT_KEYS = {DOCCOMMENT};
    private static final TextAttributesKey[] BRACES_KEYS = {BRACES, PARENS};
    private static final TextAttributesKey[] EMPTY_KEYS = TextAttributesKey.EMPTY_ARRAY;

    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == MakefileTypes.DOC_COMMENT) {
            return DOCCOMMENT_KEYS;
        }
        if (tokenType == MakefileTypes.COMMENT) {
            return COMMENT_KEYS;
        }
        if (tokenType == MakefileTypes.TARGET) {
            return TARGET_KEYS;
        }
        if (tokenType == MakefileTypes.COLON || tokenType == MakefileTypes.ASSIGN || tokenType == MakefileTypes.SEMICOLON
            || tokenType == MakefileTypes.PIPE) {
            return SEPARATOR_KEYS;
        }
        if (tokenType == MakefileTypes.KEYWORD_INCLUDE || tokenType == MakefileTypes.KEYWORD_IFEQ || tokenType == MakefileTypes.KEYWORD_IFNEQ
            || tokenType == MakefileTypes.KEYWORD_IFDEF || tokenType == MakefileTypes.KEYWORD_IFNDEF || tokenType == MakefileTypes.KEYWORD_ELSE
            || tokenType == MakefileTypes.KEYWORD_ENDIF || tokenType == MakefileTypes.KEYWORD_DEFINE || tokenType == MakefileTypes.KEYWORD_ENDEF
            || tokenType == MakefileTypes.KEYWORD_UNDEFINE || tokenType == MakefileTypes.KEYWORD_OVERRIDE
            || tokenType == MakefileTypes.KEYWORD_EXPORT || tokenType == MakefileTypes.KEYWORD_PRIVATE
            || tokenType == MakefileTypes.KEYWORD_VPATH || tokenType == MakefileTypes.DOLLAR) {
            return KEYWORD_KEYS;
        }
        if (tokenType == MakefileTypes.PREREQUISITE) {
            return PREREQUISITE_KEYS;
        }
        if (tokenType == MakefileTypes.VARIABLE) {
            return VARIABLE_KEYS;
        }
        if (tokenType == MakefileTypes.VARIABLE_VALUE) {
            return VARIABLE_VALUE_KEYS;
        }
        if (tokenType == MakefileTypes.SPLIT) {
            return LINE_SPLIT_KEYS;
        }
        if (tokenType == MakefileTypes.TAB) {
            return TAB_KEYS;
        }
        if (tokenType == MakefileTypes.STRING) {
            return STRING_KEYS;
        }
        if (tokenType == MakefileTypes.OPEN_PAREN || tokenType == MakefileTypes.CLOSE_PAREN || tokenType == MakefileTypes.OPEN_CURLY
            || tokenType == MakefileTypes.CLOSE_CURLY || tokenType == MakefileTypes.BACKTICK) {
            return BRACES_KEYS;
        }
        if (tokenType == TokenType.BAD_CHARACTER) {
            return BAD_CHAR_KEYS;
        }
        return EMPTY_KEYS;
    }

    @Override
    public Lexer getHighlightingLexer() {
        return new MakefileLexerAdapter();
    }
}
