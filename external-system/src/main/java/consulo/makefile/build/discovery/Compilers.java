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

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Which programs are C/C++ compilers, and in which language they compile a file.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class Compilers {
    public static final String LANGUAGE_C = "C";
    public static final String LANGUAGE_CXX = "CXX";

    /**
     * GCC-compatible drivers, with an optional cross prefix ({@code arm-none-eabi-gcc}) and version suffix
     * ({@code gcc-13}, {@code clang++-18}).
     */
    private static final Pattern COMPILER = Pattern.compile(
        "(?:[\\w.+]+-)*(gcc|g\\+\\+|cc|c\\+\\+|clang|clang\\+\\+|icc|icpc|icx|icpx|emcc|em\\+\\+|tcc)(?:-\\d+(?:\\.\\d+)*)?(?:\\.exe)?"
    );

    private static final Map<String, String> EXTENSION_LANGUAGES = Map.ofEntries(
        Map.entry("c", LANGUAGE_C),
        Map.entry("i", LANGUAGE_C),
        Map.entry("m", LANGUAGE_C),
        Map.entry("cc", LANGUAGE_CXX),
        Map.entry("cp", LANGUAGE_CXX),
        Map.entry("cxx", LANGUAGE_CXX),
        Map.entry("cpp", LANGUAGE_CXX),
        Map.entry("c++", LANGUAGE_CXX),
        Map.entry("ii", LANGUAGE_CXX),
        Map.entry("mm", LANGUAGE_CXX),
        Map.entry("cppm", LANGUAGE_CXX),
        Map.entry("ixx", LANGUAGE_CXX)
    );

    private Compilers() {
    }

    /**
     * Whether a word of a command names a compiler - by its file name, so {@code /usr/bin/gcc} and {@code ccache}'s
     * {@code gcc} both do.
     */
    public static boolean isCompiler(String word) {
        return COMPILER.matcher(fileName(word)).matches();
    }

    /**
     * A C++ driver compiles {@code .c} files as C++ too.
     */
    public static boolean isCppDriver(String compiler) {
        String name = fileName(compiler);
        return name.contains("++") || name.matches("(?:[\\w.+]+-)*(icpc|icpx)(?:-[\\d.]+)?(?:\\.exe)?");
    }

    /**
     * The language a source file is compiled as, {@code null} when it is not C or C++ - an assembler file, an object.
     */
    public static @Nullable String languageOf(String fileName, String compiler, @Nullable String explicitLanguage) {
        if (explicitLanguage != null) {
            return switch (explicitLanguage) {
                case "c", "cpp-output", "objective-c" -> LANGUAGE_C;
                case "c++", "c++-cpp-output", "objective-c++" -> LANGUAGE_CXX;
                default -> null;
            };
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return null;
        }
        String extension = fileName.substring(dot + 1);
        // .C is C++, .c is C
        String language = "C".equals(extension) ? LANGUAGE_CXX : EXTENSION_LANGUAGES.get(extension.toLowerCase(Locale.ROOT));
        if (LANGUAGE_C.equals(language) && isCppDriver(compiler)) {
            return LANGUAGE_CXX;
        }
        return language;
    }

    private static String fileName(String path) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slash < 0 ? path : path.substring(slash + 1);
    }
}
