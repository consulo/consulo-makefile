package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.fileEditor.structureView.tree.TreeElement;
import consulo.language.psi.PsiElement;
import consulo.navigation.ItemPresentation;
import consulo.navigation.NavigationItem;

import java.util.Objects;

public class MakefileStructureViewElement implements StructureViewTreeElement {
    private final PsiElement myElement;

    public MakefileStructureViewElement(PsiElement element) {
        myElement = element;
    }

    @Override
    public ItemPresentation getPresentation() {
        if (myElement instanceof MakefileTarget target) {
            return new MakefileTargetStructureViewPresentation(target);
        }
        return Objects.requireNonNull(((NavigationItem) myElement).getPresentation());
    }

    @Override
    public TreeElement[] getChildren() {
        if (myElement instanceof MakefileFile file) {
            return file.getTargets().stream().map(MakefileStructureViewElement::new).toArray(TreeElement[]::new);
        }
        else {
            return TreeElement.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canNavigate() {
        return myElement instanceof NavigationItem item && item.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return myElement instanceof NavigationItem item && item.canNavigateToSource();
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (myElement instanceof NavigationItem item) {
            item.navigate(requestFocus);
        }
    }

    @Override
    public PsiElement getValue() {
        return myElement;
    }
}
