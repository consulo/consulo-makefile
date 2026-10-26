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
package consulo.makefile.build.discovery;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What a compiler knows before it reads the first line: the macros it predefines and where it looks for
 * {@code #include <...>} - as {@code cc -E -v -dM} prints them.
 *
 * @param systemIncludePaths the directories of {@code #include <...>}, in search order
 * @param macros             the object-like predefined macros
 * @param target             the target triple the compiler compiles for, {@code null} when it does not say
 * @param versionMacro       the value of {@code __cplusplus}, or of {@code __STDC_VERSION__} for C - which says the
 *                           standard the compiler defaults to - {@code null} when not defined
 * @author VISTALL
 * @since 2026-10-25
 */
public record CompilerBuiltins(List<String> systemIncludePaths,
                               Map<String, String> macros,
                               @Nullable String target,
                               @Nullable String versionMacro) {
    public static final CompilerBuiltins EMPTY = new CompilerBuiltins(List.of(), Map.of(), null, null);

    /**
     * Macros which follow the language standard of the file: the language version predefines them.
     */
    private static final Set<String> STANDARD_MACROS = Set.of("__cplusplus", "__STDC__", "__STDC_VERSION__");

    private static final String SEARCH_START = "#include <...> search starts here:";
    private static final String SEARCH_END = "End of search list.";
    private static final String FRAMEWORK_SUFFIX = " (framework directory)";
    private static final String TARGET_PREFIX = "Target: ";

    /**
     * The arguments to probe a compiler with, for a {@code language} of {@link Compilers}: an empty input, read as the
     * language, preprocessed with the macros dumped and the search list shown.
     */
    public static List<String> probeArguments(String language, List<String> probeArguments, String emptyInput) {
        List<String> arguments = new ArrayList<>(probeArguments);
        arguments.add("-x");
        arguments.add(Compilers.LANGUAGE_CXX.equals(language) ? "c++" : "c");
        arguments.add("-E");
        arguments.add("-v");
        arguments.add("-dM");
        arguments.add(emptyInput);
        return arguments;
    }

    /**
     * @param stdout the {@code #define} lines
     * @param stderr the {@code -v} report: the target and the search list
     */
    public static CompilerBuiltins parse(String stdout, String stderr) {
        Map<String, String> macros = new LinkedHashMap<>();
        String versionMacro = null;
        for (String line : stdout.split("\\r?\\n")) {
            if (!line.startsWith("#define ")) {
                continue;
            }
            String definition = line.substring("#define ".length());
            int space = definition.indexOf(' ');
            String name = space < 0 ? definition : definition.substring(0, space);
            String value = space < 0 ? "" : definition.substring(space + 1);
            if (name.equals("__cplusplus") || name.equals("__STDC_VERSION__") && versionMacro == null) {
                versionMacro = value;
            }
            // a function-like macro does not fit a NAME=value define
            if (name.contains("(") || STANDARD_MACROS.contains(name)) {
                continue;
            }
            macros.put(name, value);
        }

        List<String> systemIncludePaths = new ArrayList<>();
        String target = null;
        boolean inSearchList = false;
        for (String line : stderr.split("\\r?\\n")) {
            if (line.startsWith(TARGET_PREFIX)) {
                target = line.substring(TARGET_PREFIX.length()).trim();
            }
            else if (line.equals(SEARCH_START)) {
                inSearchList = true;
            }
            else if (line.equals(SEARCH_END)) {
                inSearchList = false;
            }
            else if (inSearchList && line.startsWith(" ")) {
                String path = line.trim();
                if (path.endsWith(FRAMEWORK_SUFFIX)) {
                    path = path.substring(0, path.length() - FRAMEWORK_SUFFIX.length());
                }
                systemIncludePaths.add(path);
            }
        }
        return new CompilerBuiltins(systemIncludePaths, macros, target == null || target.isEmpty() ? null : target, versionMacro);
    }
}
