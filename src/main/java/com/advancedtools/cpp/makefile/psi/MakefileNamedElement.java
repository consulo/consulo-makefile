package com.advancedtools.cpp.makefile.psi;

import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.PsiReference;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: maxim
 * Date: 01.04.2010
 * Time: 0:05:51
 */
public class MakefileNamedElement extends MakefilePsiElement implements PsiNamedElement
{
	private static TokenSet ourSet = TokenSet.create(MakefileTokenTypes.TARGET_IDENTIFIER, MakefileTokenTypes.VAR_DEFINITION);

	public MakefileNamedElement(@org.jetbrains.annotations.NotNull ASTNode astNode)
	{
		super(astNode);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences()
	{
		return new PsiReference[]{new MakefileIdentifierReference(this)};
	}

	public PsiElement setName(@NonNls String s) throws IncorrectOperationException
	{
		throw new IncorrectOperationException("not supported");
	}

	@Override
	public String getName()
	{
		PsiElement psiElement = findNameElement();
		return psiElement != null ? psiElement.getText() : null;
	}

	public PsiElement findNameElement()
	{
		ASTNode child = getNode().findChildByType(ourSet);
		return child != null ? child.getPsi() : null;
	}
}
