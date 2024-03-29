package com.advancedtools.cpp.makefile.psi;

import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import consulo.application.util.CachedValue;
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.*;
import consulo.language.util.IncorrectOperationException;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: maxim
 * Date: 30.03.2010
 * Time: 14:48:00
 */
public class MakefileIdentifierReference implements PsiPolyVariantReference
{
	private PsiElement myElement;

	public MakefileIdentifierReference(PsiElement psiElement)
	{
		myElement = psiElement;
	}

	public PsiElement getElement()
	{
		return myElement;
	}

	public TextRange getRangeInElement()
	{
		return new TextRange(0, myElement.getTextLength());
	}

	public PsiElement resolve()
	{
		ResolveResult[] resolveResults = multiResolve(false);
		if(resolveResults.length == 1)
			return resolveResults[0].getElement();
		return null;
	}

	private boolean isSelfReference()
	{
		return isSelfReferenceType(type(myElement));
	}

	public static boolean isSelfReferenceType(IElementType iElementType)
	{
		return iElementType == MakefileTokenTypes.TARGET_IDENTIFIER || iElementType == MakefileTokenTypes.VAR_DEFINITION;
	}

	public String getCanonicalText()
	{
		return myElement.getText();
	}

	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException
	{
		return null;
	}

	public boolean isReferenceTo(PsiElement psiElement)
	{
		PsiManager m = psiElement.getManager();
		for(ResolveResult r : multiResolve(false))
		{
			if(m.areElementsEquivalent(psiElement, r.getElement()))
				return true;
		}
		return false;
	}

	@NotNull
	public Object[] getVariants()
	{
		if(isSelfReference())
		{
			return new Object[0];
		}
		CachedValue<Map<String, Object>> mapCachedValue = getDeclarationsMap();
		Map<String, Object> declMap = mapCachedValue.getValue();
		return declMap.keySet().toArray();
	}

	public boolean isSoft()
	{
		return true;
	}

	private static final Key<CachedValue<Map<String, Object>>> ourTargets = Key.create("targets");

	@NotNull
	public ResolveResult[] multiResolve(boolean b)
	{
		if(isSelfReference())
		{
			return new ResolveResult[]{new PsiElementResolveResult(myElement)};
		}
		CachedValue<Map<String, Object>> mapCachedValue = getDeclarationsMap();

		Object o = mapCachedValue.getValue().get(getCanonicalText());

		if(o instanceof PsiElement)
		{
			return new ResolveResult[]{new PsiElementResolveResult((PsiElement) o)};
		}
		else if(o != null)
		{
			Object[] objects = (Object[]) o;
			ResolveResult r[] = new ResolveResult[objects.length];
			for(int i = 0; i < r.length; ++i)
			{
				r[i] = new PsiElementResolveResult((PsiElement) objects[i]);
			}
			return r;
		}
		return ResolveResult.EMPTY_ARRAY;
	}

	private CachedValue<Map<String, Object>> getDeclarationsMap()
	{
		PsiFile containingFile = myElement.getContainingFile();
		PsiFile originalFile = containingFile.getOriginalFile();
		if(originalFile != null)
			containingFile = originalFile;
		final PsiFile psiFile = containingFile;
		CachedValue<Map<String, Object>> mapCachedValue = psiFile.getUserData(ourTargets);

		if(mapCachedValue == null)
		{
			CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(psiFile.getManager().getProject());
			psiFile.putUserData(ourTargets, mapCachedValue = cachedValuesManager.createCachedValue(new CachedValueProvider<Map<String, Object>>()
			{
				public Result<Map<String, Object>> compute()
				{
					final Map<String, Object> result = new HashMap<String, Object>();
					psiFile.acceptChildren(new PsiElementVisitor()
					{
						@Override
						public void visitElement(PsiElement element)
						{
							if(element instanceof MakefileNamedElement)
							{
								PsiElement name = ((MakefileNamedElement) element).findNameElement();
								if(name == null)
									return;

								String key = name.getText();
								Object o = result.get(key);
								if(o == null)
									result.put(key, element);
								else if(o instanceof PsiElement)
								{
									result.put(key, new Object[]{
											element,
											o
									});
								}
								else
								{
									Object[] prevArr = (Object[]) o;
									Object[] arr = new Object[prevArr.length + 1];
									System.arraycopy(prevArr, 0, arr, 0, prevArr.length);
									arr[prevArr.length] = element;
									result.put(key, arr);
								}
							}
							else
							{
								element.acceptChildren(this);
							}
						}
					});
					return new Result<Map<String, Object>>(result, psiFile);
				}
			}, false));
		}
		return mapCachedValue;
	}

	public static
	@Nullable
	IElementType type(PsiElement element)
	{
		ASTNode node = element.getNode();
		return node != null ? node.getElementType() : null;
	}
}
