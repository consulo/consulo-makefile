package com.advancedtools.cpp.makefile;

import com.advancedtools.cpp.makefile.psi.MakefileIdentifierReference;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.IElementType;
import consulo.language.findUsage.FindUsagesProvider;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author maxim
 * Date: 2/3/12
 * Time: 1:34 PM
 */
@ExtensionImpl
public class MakefileFindUsagesProvider implements FindUsagesProvider
{
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
	{
		return MakefileIdentifierReference.isSelfReferenceType(MakefileIdentifierReference.type(psiElement));
	}

	@NotNull
	public String getType(@NotNull PsiElement psiElement)
	{
		IElementType iElementType = MakefileIdentifierReference.type(psiElement);
		if(iElementType == MakefileTokenTypes.VAR_DEFINITION)
		{
			return "definition";
		}
		if(iElementType == MakefileTokenTypes.TARGET_IDENTIFIER)
		{
			return "target";
		}
		return "should not happen type";
	}

	@NotNull
	public String getDescriptiveName(@NotNull PsiElement psiElement)
	{
		return psiElement.getText();
	}

	@NotNull
	public String getNodeText(@NotNull PsiElement psiElement, boolean b)
	{
		return getDescriptiveName(psiElement);
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return MakefileLanguage.INSTANCE;
	}
}
