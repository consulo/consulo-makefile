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

import org.jspecify.annotations.NullMarked;

/**
 * The C/C++ part of the Make import: the compile settings it finds become those of the C/C++ module extension. Loaded
 * only with the C/C++ plugin.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@NullMarked
module consulo.makefile.cpp {
    requires consulo.makefile;
    requires consulo.makefile.external.system;
    requires consulo.cpp.api;
    requires consulo.annotation;
    requires consulo.application.api;
    requires consulo.disposer.api;
    requires consulo.external.system.api;
    requires consulo.localize.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.project.api;
    requires consulo.ui.api;
    requires consulo.util.lang;
    requires org.jdom;

    exports consulo.makefile.cpp;
    exports consulo.makefile.cpp.localize;
    exports consulo.makefile.cpp.module;
}
