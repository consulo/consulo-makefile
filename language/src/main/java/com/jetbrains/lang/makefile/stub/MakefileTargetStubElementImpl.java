package com.jetbrains.lang.makefile.stub;

import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.language.psi.stub.NamedStubBase;
import consulo.language.psi.stub.StubElement;
import org.jspecify.annotations.Nullable;

public class MakefileTargetStubElementImpl extends NamedStubBase<MakefileTarget> implements MakefileTargetStubElement {
    public MakefileTargetStubElementImpl(@Nullable StubElement<?> parent, @Nullable String name) {
        super(parent, MakefileTargetStubElementType.INSTANCE, name);
    }
}
