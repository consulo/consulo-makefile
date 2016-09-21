package consulo.makefile.codeInsight;

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
import com.intellij.openapi.editor.Editor;
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
			@Override
			@NotNull
			public StructureViewModel createStructureViewModel(@Nullable Editor editor)
			{
				return new TextEditorBasedStructureViewModel(psiFile)
				{
					@Override
					protected PsiFile getPsiFile()   // TODO: change abstract method to constructor parameter?
					{
						return psiFile;
					}

					@Override
					@NotNull
					public StructureViewTreeElement getRoot()
					{
						return new PsiTreeElementBase<PsiElement>(psiFile)
						{
							@Override
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
													@Override
													public void navigate(boolean b)
													{
														final Navigatable descriptor = EditSourceUtil.getDescriptor(el2.getPsi());
														if(descriptor != null)
															descriptor.navigate(b);
													}

													@Override
													public boolean canNavigate()
													{
														return true;
													}

													@Override
													public boolean canNavigateToSource()
													{
														return canNavigate();
													}

													@Override
													public Collection getChildrenBase()
													{
														return Collections.emptyList();
													}

													@Override
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

							@Override
							public String getPresentableText()
							{
								return "root";
							}
						};
					}

					@Override
					@NotNull
					public Grouper[] getGroupers()
					{
						return new Grouper[0];
					}

					@Override
					@NotNull
					public Sorter[] getSorters()
					{
						return new Sorter[]{Sorter.ALPHA_SORTER};
					}

					@Override
					@NotNull
					public Filter[] getFilters()
					{
						return new Filter[0];
					}

					@Override
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
