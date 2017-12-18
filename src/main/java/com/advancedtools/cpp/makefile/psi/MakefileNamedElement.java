package com.advancedtools.cpp.makefile.psi;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;

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
