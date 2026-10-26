package com.jetbrains.lang.makefile.stub;

import com.jetbrains.lang.makefile.MakefileLanguage;
import com.jetbrains.lang.makefile.MakefileTargetIndex;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.impl.MakefileTargetImpl;
import consulo.index.io.StringRef;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class MakefileTargetStubElementType extends IStubElementType<MakefileTargetStubElement, MakefileTarget> {
    public static final MakefileTargetStubElementType INSTANCE = new MakefileTargetStubElementType();

    private MakefileTargetStubElementType() {
        super("TARGET", MakefileLanguage.INSTANCE);
    }

    @SuppressWarnings("unused")
    public static MakefileTargetStubElementType getInstance(String debugName) {
        return INSTANCE;
    }

    @Override
    public String getExternalId() {
        return "Makefile";
    }

    @Override
    public MakefileTargetStubElement createStub(MakefileTarget psi, @Nullable StubElement parentStub) {
        return new MakefileTargetStubElementImpl(parentStub, psi.getName());
    }

    @Override
    public MakefileTarget createPsi(MakefileTargetStubElement stub) {
        return new MakefileTargetImpl(stub, stub.getStubType());
    }

    @Override
    public void indexStub(MakefileTargetStubElement stub, IndexSink sink) {
        sink.occurrence(MakefileTargetIndex.TARGET_INDEX_KEY, Objects.requireNonNull(stub.getName()));
    }

    @Override
    public void serialize(MakefileTargetStubElement stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public MakefileTargetStubElement deserialize(StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        return new MakefileTargetStubElementImpl(parentStub, StringRef.toString(dataStream.readName()));
    }
}
