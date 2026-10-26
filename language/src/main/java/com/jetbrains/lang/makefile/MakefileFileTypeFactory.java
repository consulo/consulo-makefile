/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import jakarta.inject.Inject;

/**
 * Registers {@link MakefileFileType} the way the JetBrains plugin.xml does:
 * {@code extensions="mk" fileNamesCaseInsensitive="makefile;GNUmakefile;makefile.inc;GNUmakefile.inc"}.
 */
@ExtensionImpl
public class MakefileFileTypeFactory extends FileTypeFactory {
    private static final String[] FILE_NAMES = {"makefile", "GNUmakefile", "makefile.inc", "GNUmakefile.inc"};

    private final FileNameMatcherFactory myFileNameMatcherFactory;

    @Inject
    public MakefileFileTypeFactory(FileNameMatcherFactory fileNameMatcherFactory) {
        myFileNameMatcherFactory = fileNameMatcherFactory;
    }

    @Override
    public void createFileTypes(FileTypeConsumer consumer) {
        consumer.consume(MakefileFileType.INSTANCE, "mk");
        for (String fileName : FILE_NAMES) {
            consumer.consume(MakefileFileType.INSTANCE, myFileNameMatcherFactory.createExactFileNameMatcher(fileName, true));
        }
    }
}
