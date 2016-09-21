package com.advancedtools.cpp.makefile.psi;

import org.jetbrains.annotations.NotNull;
import com.advancedtools.cpp.makefile.lang.MakefileParser;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;

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
