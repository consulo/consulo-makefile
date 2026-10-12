package consulo.makefile.codeInsight;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.fileEditor.structureView.StructureViewBuilder;
import consulo.fileEditor.structureView.StructureViewModel;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.fileEditor.structureView.TreeBasedStructureViewBuilder;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.editor.structureView.PsiStructureViewFactory;
import consulo.language.editor.structureView.PsiTreeElementBase;
import consulo.language.editor.structureView.TextEditorBasedStructureViewModel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiNavigationSupport;
import consulo.localize.LocalizeValue;
import consulo.navigation.Navigatable;
import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 2013-03-16
 */
@ExtensionImpl
public class MakefilePsiStructureViewFactory implements PsiStructureViewFactory {
    @Override
    public @Nullable StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @Override
            @NotNull
            public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
                return new TextEditorBasedStructureViewModel(psiFile) {
                    @Override
                    @NotNull
                    public StructureViewTreeElement getRoot() {
                        return new PsiTreeElementBase<PsiElement>(psiFile) {
                            @Override
                            @NotNull
                            @RequiredReadAction
                            public Collection<StructureViewTreeElement> getChildrenBase() {
                                List<StructureViewTreeElement> children = new ArrayList<>();
                                for (PsiElement el : psiFile.getChildren()) {
                                    ASTNode node = el.getNode();

                                    if (node.getElementType() == MakefileTokenTypes.STATEMENT) {
                                        for (final ASTNode el2 : node.getChildren(null)) {
                                            if (el2.getElementType() == MakefileTokenTypes.TARGET_IDENTIFIER) {
                                                children.add(new PsiTreeElementBase<>(el2.getPsi()) {
                                                    @Override
                                                    @RequiredReadAction
                                                    public void navigate(boolean b) {
                                                        Navigatable descriptor =
                                                            PsiNavigationSupport.getInstance().getDescriptor(el2.getPsi());
                                                        if (descriptor != null) {
                                                            descriptor.navigate(b);
                                                        }
                                                    }

                                                    @Override
                                                    @RequiredReadAction
                                                    public boolean canNavigate() {
                                                        return true;
                                                    }

                                                    @Override
                                                    @RequiredReadAction
                                                    public boolean canNavigateToSource() {
                                                        return canNavigate();
                                                    }

                                                    @Override
                                                    public Collection getChildrenBase() {
                                                        return Collections.emptyList();
                                                    }

                                                    @Override
                                                    public LocalizeValue getPresentableText() {
                                                        return LocalizeValue.of(el2.getText());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                return children;
                            }

                            @Override
                            public LocalizeValue getPresentableText() {
                                return LocalizeValue.localizeTODO("root");
                            }
                        };
                    }
                };
            }
        };
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
