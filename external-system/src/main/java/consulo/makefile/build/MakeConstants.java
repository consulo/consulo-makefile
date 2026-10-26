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
package consulo.makefile.build;

import consulo.makefile.icon.MakeFileIconGroup;
import consulo.makefile.build.localize.MakeBuildLocalize;
import consulo.externalSystem.model.ProjectSystemId;

import java.util.List;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public interface MakeConstants {
    ProjectSystemId SYSTEM_ID = new ProjectSystemId("MAKE", MakeBuildLocalize.makeSystemName(), MakeFileIconGroup.makefile());

    /**
     * The names GNU make looks for, in its order.
     */
    List<String> MAKEFILE_NAMES = List.of("GNUmakefile", "makefile", "Makefile");

    String CONFIGURE = "configure";

    /**
     * What autoconf makes {@link #CONFIGURE} from.
     */
    List<String> CONFIGURE_SOURCES = List.of("configure.ac", "configure.in");

    /**
     * The script of a project which generates its configure script itself.
     */
    String AUTOGEN = "autogen.sh";

    String MAKE_EXECUTABLE = "make";
}
