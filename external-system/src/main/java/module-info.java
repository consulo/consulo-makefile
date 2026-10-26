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
 * @author VISTALL
 * @since 2026-10-25
 */
@NullMarked
module consulo.makefile.external.system {
    requires consulo.makefile;
    requires consulo.external.system.api;
    requires consulo.annotation;
    requires consulo.application.api;
    requires consulo.component.api;
    requires consulo.disposer.api;
    requires consulo.execution.api;
    requires consulo.file.chooser.api;
    requires consulo.language.api;
    requires consulo.localize.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.process.api;
    requires consulo.project.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.util.io;
    requires consulo.util.lang;
    requires consulo.util.xml.serializer;
    requires consulo.virtual.file.system.api;

    requires com.google.gson;

    exports consulo.makefile.build;
    exports consulo.makefile.build.discovery;
    exports consulo.makefile.build.importProvider;
    exports consulo.makefile.build.localize;
    exports consulo.makefile.build.module;
    exports consulo.makefile.build.run;
    exports consulo.makefile.build.service;
    exports consulo.makefile.build.setting;
    exports consulo.makefile.build.ui;

    opens consulo.makefile.build.setting to consulo.util.xml.serializer;
}
