package consulo.makefile.codeInsight;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.advancedtools.cpp.makefile.MakefileSyntaxHighlighter;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.highlight.SingleLazyInstanceSyntaxHighlighterFactory;
import consulo.language.editor.highlight.SyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 18:52/16.03.13
 */
@ExtensionImpl
public class MakefileSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@NotNull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new MakefileSyntaxHighlighter();
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return MakefileLanguage.INSTANCE;
	}
}
