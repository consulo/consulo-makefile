package com.advancedtools.cpp.makefile.psi;

import com.advancedtools.cpp.makefile.lang.MakefileParser;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * User: maxim
 * Date: 01.04.2010
 * Time: 0:09:05
 */
public class MakefilePsiElement extends ASTWrapperPsiElement
{
	public MakefilePsiElement(ASTNode astNode)
	{
		super(astNode);
	}

	public String toString()
	{
		return "Composite:" + getNode().getElementType();
	}

	@NotNull
	@Override
	public PsiReference[] getReferences()
	{
		if(MakefileParser.shouldProduceComposite(getNode().getElementType()))
			return new PsiReference[]{new MakefileIdentifierReference(this)};
		return super.getReferences();
	}
}
