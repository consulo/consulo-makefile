package com.jetbrains.lang.makefile;

import consulo.language.editor.structureView.StructureViewModelBase;
import consulo.language.psi.PsiFile;

public class MakefileStructureViewModel extends StructureViewModelBase {
    public MakefileStructureViewModel(PsiFile psiFile) {
        super(psiFile, new MakefileStructureViewElement(psiFile));
    }
}
