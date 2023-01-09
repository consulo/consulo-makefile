/* AdvancedTools, 2007, all rights reserved */
package com.advancedtools.cpp.makefile;

import com.advancedtools.cpp.makefile.lang.MakefileLexer;
import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maxim
 */
public class MakefileSyntaxHighlighter extends SyntaxHighlighterBase
{
	private static Map<IElementType, TextAttributesKey> keys1;
	private static Map<IElementType, TextAttributesKey> keys2;

	public static final TextAttributesKey MAKEFILE_KEYWORD = TextAttributesKey.createTextAttributesKey("MAKEFILE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

	static final TextAttributesKey MAKEFILE_LINE_COMMENT = TextAttributesKey.createTextAttributesKey("MAKEFILE_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

	static final TextAttributesKey MAKEFILE_TEMPLATE_DATA = TextAttributesKey.createTextAttributesKey("MAKEFILE_TEMPLATE_DATA", DefaultLanguageHighlighterColors.NUMBER);

	static final TextAttributesKey MAKEFILE_TARGET = TextAttributesKey.createTextAttributesKey("MAKEFILE_TARGET", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

	static final TextAttributesKey MAKEFILE_DEFINITION = TextAttributesKey.createTextAttributesKey("MAKEFILE_DEFINITION", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);

	static
	{
		keys1 = new HashMap<IElementType, TextAttributesKey>();
		keys2 = new HashMap<IElementType, TextAttributesKey>();

		safeMap(keys1, MakefileTokenTypes.KEYWORDS, MAKEFILE_KEYWORD);
		keys1.put(MakefileTokenTypes.END_OF_LINE_COMMENT, MAKEFILE_LINE_COMMENT);
		keys1.put(MakefileTokenTypes.TEMPLATE_DATA, MAKEFILE_TEMPLATE_DATA);
		keys1.put(MakefileTokenTypes.TARGET_IDENTIFIER_PART, MAKEFILE_TARGET);
		keys1.put(MakefileTokenTypes.VAR_DEFINITION, MAKEFILE_DEFINITION);
	}

	@NotNull
	public Lexer getHighlightingLexer()
	{
		return new MakefileLexer(true);
	}

	@NotNull
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
	{
		return pack(keys1.get(tokenType), keys2.get(tokenType));
	}
}
