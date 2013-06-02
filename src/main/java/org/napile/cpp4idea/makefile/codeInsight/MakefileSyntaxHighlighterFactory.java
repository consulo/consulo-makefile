package org.napile.cpp4idea.makefile.codeInsight;

import org.jetbrains.annotations.NotNull;
import com.advancedtools.cpp.makefile.MakefileSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 18:52/16.03.13
 */
public class MakefileSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@NotNull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new MakefileSyntaxHighlighter();
	}
}
