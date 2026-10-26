package com.jetbrains.lang.makefile.psi.impl;

import com.jetbrains.lang.makefile.MakefileTargetReference;
import com.jetbrains.lang.makefile.psi.MakefilePrerequisite;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.path.FileReference;
import consulo.language.psi.path.FileReferenceSet;
import consulo.util.collection.ArrayUtil;

public abstract class MakefilePrerequisiteMixin extends ASTWrapperPsiElement implements MakefilePrerequisite {
    MakefilePrerequisiteMixin(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference[] getReferences() {
        MakefileTargetReference targetReference = new MakefileTargetReference(this);
        if (isPhonyTarget()) {
            return new PsiReference[]{targetReference};
        }
        FileReference[] references = new FileReferenceSet(this).getAllReferences();
        return ArrayUtil.prepend(targetReference, references, PsiReference.ARRAY_FACTORY);
    }
}
