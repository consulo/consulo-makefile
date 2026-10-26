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

import com.jetbrains.lang.makefile.stub.MakefileTargetStubElementType;

/**
 * The stub element types of the language - {@link MakefileStubElementTypeHolder} loads every field. The JetBrains
 * plugin registers {@code MakefileTypes} as the holder, but a Consulo holder may declare stub element types only.
 */
public interface MakefileStubElementTypes {
    MakefileTargetStubElementType TARGET = MakefileTargetStubElementType.INSTANCE;
}
