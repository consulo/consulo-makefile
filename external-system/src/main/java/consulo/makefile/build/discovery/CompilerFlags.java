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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What the arguments of a GCC-compatible compiler say about its sources: include directories, defines, the language
 * standard and the target - and which of them change what the compiler predefines, so it can be asked with the same.
 *
 * @param includePaths       {@code -I}, {@code -iquote} and {@code -idirafter} directories, absolute
 * @param systemIncludePaths {@code -isystem} directories, absolute
 * @param defineChanges      the defines in command order: a {@code null} value undefines ({@code -U})
 * @param standard           the {@code -std=} value - {@code gnu11}, {@code c++20} - {@code null} when none is given
 * @param target             the target triple of a cross compiling Clang, {@code null} when none is given
 * @param sources            the C and C++ files the command compiles
 * @param probeArguments     the arguments which change the predefined macros and the system include directories
 * @param preprocessOnly     whether the command only preprocesses ({@code -E}) or lists dependencies ({@code -M})
 * @author VISTALL
 * @since 2026-10-25
 */
public record CompilerFlags(List<String> includePaths,
                            List<String> systemIncludePaths,
                            Map<String, @Nullable String> defineChanges,
                            @Nullable String standard,
                            @Nullable String target,
                            List<Source> sources,
                            List<String> probeArguments,
                            boolean preprocessOnly) {
    /**
     * @param language {@link Compilers#LANGUAGE_C} or {@link Compilers#LANGUAGE_CXX}
     */
    public record Source(Path file, String language) {
    }

    /**
     * Options whose value is the next argument, which is not a source then.
     */
    private static final Set<String> OPTIONS_WITH_ARGUMENT = Set.of(
        "-o", "-MF", "-MT", "-MQ", "-include", "-imacros", "-iprefix", "-iwithprefix", "-iwithprefixbefore",
        "-Xclang", "-Xpreprocessor", "-Xlinker", "-Xassembler", "-Xarch_host", "-arch", "-aux-info", "-dumpbase",
        "-dumpdir", "-L", "-l", "-u", "-T", "-z", "-e", "-soname", "-main-file-name", "-ivfsoverlay"
    );

    /**
     * {@code -f} options which change a predefined macro: {@code __EXCEPTIONS}, {@code __GXX_RTTI}, {@code _OPENMP},
     * {@code __CHAR_UNSIGNED__}, {@code __pic__}...
     */
    private static final Set<String> PROBED_F_OPTIONS = Set.of(
        "exceptions", "rtti", "openmp", "signed-char", "unsigned-char", "short-wchar", "short-enums", "pic", "PIC",
        "pie", "PIE", "freestanding", "fast-math", "finite-math-only", "char8_t", "gnu-keywords", "ms-extensions",
        "sized-deallocation", "coroutines", "modules-ts", "threadsafe-statics", "trapping-math", "wrapv"
    );

    private static final int MAX_RESPONSE_FILE_DEPTH = 4;

    public static CompilerFlags parse(String compiler, List<String> arguments, Path directory) {
        List<String> expanded = new ArrayList<>();
        expandResponseFiles(arguments, directory, expanded, 0);

        List<String> includePaths = new ArrayList<>();
        List<String> systemIncludePaths = new ArrayList<>();
        Map<String, @Nullable String> defineChanges = new LinkedHashMap<>();
        Set<String> probeArguments = new LinkedHashSet<>();
        List<Source> sources = new ArrayList<>();
        String standard = null;
        String target = null;
        String explicitLanguage = null;
        boolean preprocessOnly = false;

        for (int i = 0; i < expanded.size(); i++) {
            String argument = expanded.get(i);
            String next = i + 1 < expanded.size() ? expanded.get(i + 1) : null;

            if (!argument.startsWith("-") || argument.equals("-")) {
                String language = Compilers.languageOf(argument, compiler, explicitLanguage);
                if (language != null) {
                    sources.add(new Source(directory.resolve(argument).normalize(), language));
                }
                continue;
            }

            if (argument.equals("-E") || argument.equals("-M") || argument.equals("-MM")) {
                preprocessOnly = true;
                continue;
            }

            if (argument.startsWith("-D")) {
                String value = optionValue(argument, "-D", next);
                if (value != null) {
                    if (argument.equals("-D")) {
                        i++;
                    }
                    int eq = value.indexOf('=');
                    // -DNAME defines NAME as 1, like the compiler does
                    if (eq < 0) {
                        defineChanges.remove(value);
                        defineChanges.put(value, "1");
                    }
                    else {
                        String name = value.substring(0, eq);
                        defineChanges.remove(name);
                        defineChanges.put(name, value.substring(eq + 1));
                    }
                }
                continue;
            }
            if (argument.startsWith("-U")) {
                String value = optionValue(argument, "-U", next);
                if (value != null) {
                    if (argument.equals("-U")) {
                        i++;
                    }
                    defineChanges.remove(value);
                    defineChanges.put(value, null);
                }
                continue;
            }

            String include = null;
            boolean system = false;
            int consumed = 0;
            for (String prefix : List.of("-isystem", "-iquote", "-idirafter", "-I")) {
                if (argument.startsWith(prefix)) {
                    include = optionValue(argument, prefix, next);
                    system = prefix.equals("-isystem");
                    consumed = argument.equals(prefix) ? 1 : 0;
                    break;
                }
            }
            if (include != null) {
                i += consumed;
                (system ? systemIncludePaths : includePaths).add(directory.resolve(include).normalize().toString());
                continue;
            }

            if (argument.startsWith("-std=")) {
                standard = argument.substring("-std=".length());
                probeArguments.add(argument);
                continue;
            }
            if (argument.startsWith("--target=")) {
                target = argument.substring("--target=".length());
                probeArguments.add(argument);
                continue;
            }
            if ((argument.equals("-target") || argument.equals("--target")) && next != null) {
                target = next;
                probeArguments.add("--target=" + next);
                i++;
                continue;
            }
            if (argument.equals("-x") && next != null) {
                explicitLanguage = next.equals("none") ? null : next;
                i++;
                continue;
            }
            if (argument.startsWith("-x") && argument.length() > 2) {
                String language = argument.substring(2);
                explicitLanguage = language.equals("none") ? null : language;
                continue;
            }
            if ((argument.equals("--sysroot") || argument.equals("-isysroot")) && next != null) {
                probeArguments.add(argument.equals("--sysroot") ? "--sysroot=" + directory.resolve(next).normalize() : "-isysroot");
                if (argument.equals("-isysroot")) {
                    probeArguments.add(directory.resolve(next).normalize().toString());
                }
                i++;
                continue;
            }
            if (OPTIONS_WITH_ARGUMENT.contains(argument)) {
                i++;
                continue;
            }
            if (isProbed(argument)) {
                probeArguments.add(argument);
            }
        }
        return new CompilerFlags(includePaths, systemIncludePaths, defineChanges, standard, target, sources,
            new ArrayList<>(probeArguments), preprocessOnly);
    }

    private static boolean isProbed(String argument) {
        if (argument.equals("-ansi") || argument.equals("-pthread") || argument.startsWith("-O") || argument.startsWith("-m")
            || argument.startsWith("--sysroot=") || argument.startsWith("-nostdinc") || argument.startsWith("-stdlib=")) {
            return true;
        }
        if (argument.startsWith("-fno-")) {
            return PROBED_F_OPTIONS.contains(argument.substring("-fno-".length()));
        }
        if (argument.startsWith("-f")) {
            return PROBED_F_OPTIONS.contains(argument.substring("-f".length()));
        }
        return false;
    }

    private static @Nullable String optionValue(String argument, String prefix, @Nullable String next) {
        if (argument.length() > prefix.length()) {
            return argument.substring(prefix.length());
        }
        return next;
    }

    /**
     * {@code @file} reads arguments from a file - as the compiler would, when it exists; otherwise it is an argument.
     */
    private static void expandResponseFiles(List<String> arguments, Path directory, List<String> result, int depth) {
        for (String argument : arguments) {
            if (argument.length() > 1 && argument.charAt(0) == '@' && depth < MAX_RESPONSE_FILE_DEPTH) {
                Path file = directory.resolve(argument.substring(1));
                if (Files.isRegularFile(file)) {
                    try {
                        List<String> words = ShellWords.splitWords(Files.readString(file).replace('\n', ' '), command -> null);
                        expandResponseFiles(words, directory, result, depth + 1);
                        continue;
                    }
                    catch (IOException ignored) {
                    }
                }
            }
            result.add(argument);
        }
    }
}
