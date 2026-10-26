package com.jetbrains.lang.makefile;

import consulo.language.ast.IElementType;
import consulo.language.ast.TokenType;
import consulo.language.impl.parser.GeneratedParserUtilBase;
import consulo.language.parser.PsiBuilder;

import java.util.Set;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.*;

@SuppressWarnings("UnusedParameters")
public class MakefileParserUtil extends GeneratedParserUtilBase {
    private static final Set<IElementType> NON_TARGET_TOKENS = Set.of(EOL, COLON, TAB, SPLIT, RECIPE_PREFIX);
    private static final Set<IElementType> NON_PREREQ_TOKENS =
        Set.of(EOL, TAB, COLON, OPEN_CURLY, CLOSE_CURLY, ASSIGN, STRING, PIPE, SEMICOLON, SPLIT);
    private static final Set<IElementType> NON_IDENTIFIER_TOKENS =
        Set.of(EOL, TAB, SPLIT, COLON, OPEN_PAREN, CLOSE_PAREN, OPEN_CURLY, CLOSE_CURLY, ASSIGN, STRING, COMMA, RECIPE_PREFIX);

    // targets
    public static boolean parseNoWhitespaceOrColon(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_TARGET_TOKENS, false, false);
    }

    public static boolean parseToDollarNoWhitespaceOrColon(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_TARGET_TOKENS, false, true);
    }

    public static boolean parseNoWhitespaceOrColonBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return true;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_TARGET_TOKENS, true, false);
    }

    public static boolean parseToDollarNoWhitespaceOrColonBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return false;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_TARGET_TOKENS, true, true);
    }

    // prerequisites
    public static boolean parsePrereqNoWhitespace(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_PREREQ_TOKENS, false, false);
    }

    public static boolean parsePrereqToDollarNoWhitespace(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_PREREQ_TOKENS, false, true);
    }

    public static boolean parsePrereqNoWhitespaceBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return true;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_PREREQ_TOKENS, true, false);
    }

    public static boolean parsePrereqToDollarNoWhitespaceBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return false;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_PREREQ_TOKENS, true, true);
    }

    // identifiers
    public static boolean parseNoWhitespace(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_IDENTIFIER_TOKENS, false, false);
    }

    public static boolean parseToDollarNoWhitespace(PsiBuilder builder, int level) {
        return consumeAllNonWsExceptTokens(builder, level, NON_IDENTIFIER_TOKENS, false, true);
    }

    public static boolean parseNoWhitespaceBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return true;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_IDENTIFIER_TOKENS, true, false);
    }

    public static boolean parseToDollarNoWhitespaceBehind(PsiBuilder builder, int level) {
        if (isWhitespaceBehind(builder)) {
            return false;
        }
        return consumeAllNonWsExceptTokens(builder, level, NON_IDENTIFIER_TOKENS, true, true);
    }

    private static boolean isWhitespaceBehind(PsiBuilder builder) {
        return builder.rawLookup(0) == TokenType.WHITE_SPACE ||
            builder.rawLookup(-1) == TokenType.WHITE_SPACE;
    }

    private static boolean consumeAllNonWsExceptTokens(PsiBuilder builder,
                                                       int level,
                                                       Set<IElementType> tokens,
                                                       boolean allowEmpty,
                                                       boolean errorOnWs) {
        // accept everything till the end of line
        boolean hasAny = allowEmpty;
        do {
            if (builder.getTokenType() == DOLLAR) {
                IElementType lookAhead = builder.lookAhead(1);
                if (lookAhead == OPEN_CURLY || lookAhead == OPEN_PAREN) {
                    return hasAny;
                }
            }
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null || tokens.contains(tokenType)) {
                return hasAny;
            }
            if (builder.rawLookup(1) == TokenType.WHITE_SPACE) {
                if (errorOnWs) {
                    return false;
                }
                builder.advanceLexer();
                return true;
            }
            builder.advanceLexer();
            hasAny = true;
        }
        while (true);
    }

    public static boolean parseLine(PsiBuilder builder, int level) {
        return parseLineTokens(builder, Set.of(EOL, BACKTICK, DOUBLEQUOTE, QUOTE));
    }

    public static boolean parseLineNotEndef(PsiBuilder builder, int level) {
        return parseLineTokens(builder, Set.of(EOL, KEYWORD_ENDEF, DOUBLEQUOTE, QUOTE));
    }

    public static boolean parseDoubleQuotedString(PsiBuilder builder, int level) {
        return parseLineTokens(builder, Set.of(EOL, DOUBLEQUOTE));
    }

    public static boolean parseSingleQuotedString(PsiBuilder builder, int level) {
        return parseLineTokens(builder, Set.of(EOL, QUOTE));
    }

    private static boolean parseLineTokens(PsiBuilder builder, Set<IElementType> tokens) {
        // accept everything till the end of line
        boolean hasAny = false;
        do {
            if (builder.getTokenType() == DOLLAR) {
                IElementType lookAhead = builder.lookAhead(1);
                if (lookAhead == OPEN_CURLY || lookAhead == OPEN_PAREN) {
                    return hasAny;
                }
            }
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null || tokens.contains(tokenType)) {
                return hasAny;
            }
            builder.advanceLexer();
            hasAny = true;
        }
        while (true);
    }

    public static boolean parseVariableUsageCurly(PsiBuilder builder, int level) {
        return parseVariableUsage(builder, level, true, CLOSE_CURLY);
    }

    public static boolean parseVariableUsageParen(PsiBuilder builder, int level) {
        return parseVariableUsage(builder, level, false, CLOSE_PAREN);
    }

    public static boolean parseVariableUsage(PsiBuilder builder, int level, boolean acceptFunctionNames, IElementType end) {
        int curly = 0;
        int paren = 0;
        if (builder.getTokenType() == FUNCTION_NAME) {
            if (!acceptFunctionNames) {
                return false;
            }
            builder.advanceLexer();
        }
        do {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == OPEN_PAREN) {
                paren++;
            }
            else if (tokenType == CLOSE_PAREN) {
                if (paren > 0) {
                    paren--;
                }
                else {
                    return consumeToken(builder, end);
                }
            }
            else if (tokenType == OPEN_CURLY) {
                curly++;
            }
            else if (tokenType == CLOSE_CURLY) {
                if (curly > 0) {
                    curly--;
                }
                else {
                    return consumeToken(builder, end);
                }
            }
            else if (tokenType == EOL || tokenType == null) {
                return false;
            }
            builder.advanceLexer();
        }
        while (true);
    }
}
