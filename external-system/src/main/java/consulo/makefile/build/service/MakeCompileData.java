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
package consulo.makefile.build.service;

import consulo.makefile.build.MakeConstants;
import consulo.externalSystem.model.Key;
import consulo.externalSystem.model.ProjectKeys;
import consulo.externalSystem.service.project.AbstractExternalEntityData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * How a group of the sources of a module compiles: the files whose commands agree on all of it share one.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeCompileData extends AbstractExternalEntityData {
    private static final long serialVersionUID = 1L;

    public static final Key<MakeCompileData> KEY = Key.create(MakeCompileData.class, ProjectKeys.MODULE.getProcessingWeight() + 1);

    private final String myLanguage;
    private final List<String> myIncludePaths;
    private final List<String> mySystemIncludePaths;
    private final Map<String, String> myBuiltinDefines;
    private final Map<String, String> myDefines;
    private final @Nullable String myStandard;
    private final @Nullable String myTarget;

    /**
     * @param language           {@code C} or {@code CXX}
     * @param systemIncludePaths the {@code -isystem} directories, then the compiler's own
     * @param builtinDefines     the macros the compiler predefines
     * @param defines            the defines of the command, which win over the predefined
     * @param standard         the language standard in the notation of {@link consulo.makefile.build.discovery.CompilerStandards}
     * @param target             the target triple
     */
    public MakeCompileData(String language,
                           List<String> includePaths,
                           List<String> systemIncludePaths,
                           Map<String, String> builtinDefines,
                           Map<String, String> defines,
                           @Nullable String standard,
                           @Nullable String target) {
        super(MakeConstants.SYSTEM_ID);
        myLanguage = language;
        myIncludePaths = new ArrayList<>(includePaths);
        mySystemIncludePaths = new ArrayList<>(systemIncludePaths);
        myBuiltinDefines = new LinkedHashMap<>(builtinDefines);
        myDefines = new LinkedHashMap<>(defines);
        myStandard = standard;
        myTarget = target;
    }

    public String getLanguage() {
        return myLanguage;
    }

    public List<String> getIncludePaths() {
        return List.copyOf(myIncludePaths);
    }

    public List<String> getSystemIncludePaths() {
        return List.copyOf(mySystemIncludePaths);
    }

    public Map<String, String> getBuiltinDefines() {
        return Collections.unmodifiableMap(myBuiltinDefines);
    }

    public Map<String, String> getDefines() {
        return Collections.unmodifiableMap(myDefines);
    }

    public @Nullable String getStandard() {
        return myStandard;
    }

    public @Nullable String getTarget() {
        return myTarget;
    }
}
