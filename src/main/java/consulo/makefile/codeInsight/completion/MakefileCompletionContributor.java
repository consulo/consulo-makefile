package consulo.makefile.codeInsight.completion;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.completion.*;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 20:06/16.03.13
 */
@ExtensionImpl
public class MakefileCompletionContributor extends CompletionContributor
{
	private static final String[] KEYWORDS = new String[] {"ifdef", "ifndef", "ifeq", "else", "endif", "error", "include"};

	public MakefileCompletionContributor()
	{
		extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new CompletionProvider()
		{
			@Override
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
			{
				for(String keyword : KEYWORDS)
				{
					result.addElement(LookupElementBuilder.create(keyword).bold());
				}
			}
		});
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return MakefileLanguage.INSTANCE;
	}
}
