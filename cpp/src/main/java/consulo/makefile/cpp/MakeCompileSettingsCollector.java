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
package consulo.makefile.cpp;

import consulo.cpp.lang.version.C11LanguageVersion;
import consulo.cpp.lang.version.C17LanguageVersion;
import consulo.cpp.lang.version.C23LanguageVersion;
import consulo.cpp.lang.version.C89LanguageVersion;
import consulo.cpp.lang.version.C99LanguageVersion;
import consulo.cpp.lang.version.CLanguageVersion;
import consulo.cpp.lang.version.CLanguageVersions;
import consulo.cpp.lang.version.Cpp11LanguageVersion;
import consulo.cpp.lang.version.Cpp14LanguageVersion;
import consulo.cpp.lang.version.Cpp17LanguageVersion;
import consulo.cpp.lang.version.Cpp20LanguageVersion;
import consulo.cpp.lang.version.Cpp23LanguageVersion;
import consulo.cpp.lang.version.Cpp98LanguageVersion;
import consulo.cpp.module.extension.CCompileSettings;
import consulo.makefile.build.discovery.Compilers;
import consulo.makefile.build.service.MakeCompileData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Merges the compile settings of the groups of a module into the one set the module has.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
final class MakeCompileSettingsCollector {
    /**
     * C standards, as {@link consulo.makefile.build.discovery.CompilerStandards} tells them, to language version ids.
     */
    private static final Map<String, String> C_STANDARDS = Map.of(
        "89", C89LanguageVersion.ID,
        "99", C99LanguageVersion.ID,
        "11", C11LanguageVersion.ID,
        "17", C17LanguageVersion.ID,
        "23", C23LanguageVersion.ID
    );

    /**
     * C++ standards to language version ids; there is no version of C++26 yet.
     */
    private static final Map<String, String> CPP_STANDARDS = Map.of(
        "98", Cpp98LanguageVersion.ID,
        "11", Cpp11LanguageVersion.ID,
        "14", Cpp14LanguageVersion.ID,
        "17", Cpp17LanguageVersion.ID,
        "20", Cpp20LanguageVersion.ID,
        "23", Cpp23LanguageVersion.ID,
        "26", Cpp23LanguageVersion.ID
    );

    private final Set<String> myIncludePaths = new LinkedHashSet<>();
    private final Set<String> mySystemIncludePaths = new LinkedHashSet<>();
    private @Nullable Map<String, String> myBuiltinDefines;
    private final Map<String, String> myDefines = new LinkedHashMap<>();
    private @Nullable CLanguageVersion myCStandard;
    private @Nullable CLanguageVersion myCppStandard;
    private @Nullable String myTarget;

    void add(MakeCompileData data) {
        myIncludePaths.addAll(data.getIncludePaths());
        mySystemIncludePaths.addAll(data.getSystemIncludePaths());
        myDefines.putAll(data.getDefines());

        // what every compiler of the module predefines alike - so the C++ only macros stay away from C
        Map<String, String> builtinDefines = myBuiltinDefines;
        if (builtinDefines == null) {
            myBuiltinDefines = new LinkedHashMap<>(data.getBuiltinDefines());
        }
        else {
            builtinDefines.entrySet().removeIf(entry -> !Objects.equals(data.getBuiltinDefines().get(entry.getKey()), entry.getValue()));
        }

        if (Compilers.LANGUAGE_C.equals(data.getLanguage())) {
            myCStandard = CLanguageVersions.newer(myCStandard, find(C_STANDARDS, data.getStandard()));
        }
        else {
            myCppStandard = CLanguageVersions.newer(myCppStandard, find(CPP_STANDARDS, data.getStandard()));
        }
        if (myTarget == null) {
            myTarget = data.getTarget();
        }
    }

    CCompileSettings build() {
        Set<String> includePaths = new LinkedHashSet<>(myIncludePaths);
        includePaths.addAll(mySystemIncludePaths);

        Map<String, String> defines = new LinkedHashMap<>();
        if (myBuiltinDefines != null) {
            defines.putAll(myBuiltinDefines);
        }
        defines.putAll(myDefines);

        CCompileSettings settings = new CCompileSettings();
        settings.setIncludePaths(new ArrayList<>(includePaths));
        settings.setDefines(defines);
        settings.setCStandard(myCStandard);
        settings.setCppStandard(myCppStandard);
        settings.setTarget(myTarget);
        return settings;
    }

    private static @Nullable CLanguageVersion find(Map<String, String> standards, @Nullable String standard) {
        return standard == null ? null : CLanguageVersions.findById(standards.get(standard));
    }
}
