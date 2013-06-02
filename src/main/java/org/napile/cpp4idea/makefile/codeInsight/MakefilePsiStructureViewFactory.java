package org.napile.cpp4idea.makefile.codeInsight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.EditSourceUtil;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 18:58/16.03.13
 */
public class MakefilePsiStructureViewFactory implements PsiStructureViewFactory
{
	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile)
	{
		return new TreeBasedStructureViewBuilder()
		{
			@NotNull
			public StructureViewModel createStructureViewModel()
			{
				return new TextEditorBasedStructureViewModel(psiFile)
				{
					protected PsiFile getPsiFile()   // TODO: change abstract method to constructor parameter?
					{
						return psiFile;
					}

					@NotNull
					public StructureViewTreeElement getRoot()
					{
						return new PsiTreeElementBase<PsiElement>(psiFile)
						{
							@NotNull
							public Collection<StructureViewTreeElement> getChildrenBase()
							{
								final List<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();
								for(PsiElement el : psiFile.getChildren())
								{
									final ASTNode node = el.getNode();

									if(node.getElementType() == MakefileTokenTypes.STATEMENT)
									{
										for(final ASTNode el2 : node.getChildren(null))
										{
											if(el2.getElementType() == MakefileTokenTypes.TARGET_IDENTIFIER)
											{
												children.add(new PsiTreeElementBase(el2.getPsi())
												{
													public void navigate(boolean b)
													{
														final Navigatable descriptor = EditSourceUtil.getDescriptor(el2.getPsi());
														if(descriptor != null)
															descriptor.navigate(b);
													}

													public boolean canNavigate()
													{
														return true;
													}

													public boolean canNavigateToSource()
													{
														return canNavigate();
													}

													public Collection getChildrenBase()
													{
														return Collections.emptyList();
													}

													public String getPresentableText()
													{
														return el2.getText();
													}
												});
											}
										}
									}
								}
								return children;
							}

							public String getPresentableText()
							{
								return "root";
							}
						};
					}

					@NotNull
					public Grouper[] getGroupers()
					{
						return new Grouper[0];
					}

					@NotNull
					public Sorter[] getSorters()
					{
						return new Sorter[]{Sorter.ALPHA_SORTER};
					}

					@NotNull
					public Filter[] getFilters()
					{
						return new Filter[0];
					}

					@NotNull
					protected Class[] getSuitableClasses()
					{
						return new Class[0];
					}
				};
			}
		};
	}
}
