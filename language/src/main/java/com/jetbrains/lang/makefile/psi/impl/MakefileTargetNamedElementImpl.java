package com.jetbrains.lang.makefile.psi.impl;

import com.jetbrains.lang.makefile.psi.MakefileNamedElement;
import com.jetbrains.lang.makefile.stub.MakefileTargetStubElement;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.stub.StubBasedPsiElementBase;
import consulo.language.psi.stub.IStubElementType;

public abstract class MakefileTargetNamedElementImpl extends StubBasedPsiElementBase<MakefileTargetStubElement> implements MakefileNamedElement {
    public MakefileTargetNamedElementImpl(ASTNode node) {
        super(node);
    }

    public MakefileTargetNamedElementImpl(MakefileTargetStubElement stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String toString() {
        return "MakefileTargetImpl(TARGET)";
    }
}
