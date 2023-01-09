/* AdvancedTools, 2007, all rights reserved */
package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import consulo.language.ast.TokenSet;
import consulo.language.lexer.FlexAdapter;
import consulo.language.lexer.MergingLexerAdapter;

/**
 * @author maxim
 */
public class MakefileLexer extends MergingLexerAdapter
{
	private static final TokenSet tokenSet = TokenSet.create(MakefileTokenTypes.TEMPLATE_DATA, MakefileTokenTypes.WHITE_SPACE);

	public MakefileLexer(boolean highlighting)
	{
		super(new FlexAdapter(new _MakefileLexer(highlighting)), tokenSet);
	}
}
