package org.napile.cpp4idea.makefile.codeInsight.completion;

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;

/**
 * @author VISTALL
 * @since 20:06/16.03.13
 */
public class MakefileCompletionContributor extends CompletionContributor
{
	private static final String[] KEYWORDS = new String[] {"ifdef", "ifndef", "ifeq", "else", "endif", "error", "include"};

	public MakefileCompletionContributor()
	{
		extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new CompletionProvider<CompletionParameters>()
		{
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
			{
				for(String keyword : KEYWORDS)
				{
					result.addElement(LookupElementBuilder.create(keyword).bold());
				}
			}
		});
	}
}
