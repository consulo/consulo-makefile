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

/**
 * The language standard a file compiles with: the {@code -std=} of its command, or else the default of its compiler,
 * which its {@code __cplusplus} / {@code __STDC_VERSION__} tells. The standard is the year in the notation of CMake's
 * {@code <LANG>_STANDARD} - {@code 89}, {@code 99}, {@code 11}, {@code 17}, {@code 23} for C, {@code 98} to {@code 26}
 * for C++.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class CompilerStandards {
    private CompilerStandards() {
    }

    /**
     * The standard, {@code null} when neither the flag nor the macro says.
     *
     * @param language     {@link Compilers#LANGUAGE_C} or {@link Compilers#LANGUAGE_CXX}
     * @param standard     the {@code -std=} value
     * @param versionMacro the value of {@code __cplusplus} or {@code __STDC_VERSION__} the compiler predefines
     */
    public static @Nullable String standard(String language, @Nullable String standard, @Nullable String versionMacro) {
        boolean cpp = Compilers.LANGUAGE_CXX.equals(language);
        String byFlag = standard == null ? null : byFlag(standard, cpp);
        if (byFlag != null) {
            return byFlag;
        }
        return versionMacro == null ? null : byMacro(versionMacro, cpp);
    }

    /**
     * {@code c11}, {@code gnu++17}, {@code c++2a}, {@code iso9899:1999}...
     */
    static @Nullable String byFlag(String value, boolean cpp) {
        String standard = value.toLowerCase(Locale.ROOT);
        if (standard.startsWith("iso9899:")) {
            standard = switch (standard.substring("iso9899:".length())) {
                case "1990", "199409" -> "c90";
                case "1999" -> "c99";
                case "2011" -> "c11";
                case "2017", "2018" -> "c17";
                case "2024" -> "c23";
                default -> standard;
            };
        }
        if (standard.startsWith("gnu")) {
            standard = "c" + standard.substring("gnu".length());
        }
        String c = switch (standard) {
            case "c89", "c90" -> "89";
            case "c99", "c9x" -> "99";
            case "c11", "c1x" -> "11";
            case "c17", "c18" -> "17";
            case "c23", "c2x", "clatest" -> "23";
            default -> null;
        };
        String cxx = switch (standard) {
            case "c++98", "c++03" -> "98";
            case "c++11", "c++0x" -> "11";
            case "c++14", "c++1y" -> "14";
            case "c++17", "c++1z" -> "17";
            case "c++20", "c++2a" -> "20";
            case "c++23", "c++2b", "c++latest" -> "23";
            case "c++26", "c++2c" -> "26";
            default -> null;
        };
        // a C standard means nothing to C++, and the other way round
        return cpp ? cxx : c;
    }

    /**
     * {@code 201703L} and the like - the newest standard the value reaches.
     */
    static @Nullable String byMacro(String value, boolean cpp) {
        String digits = value.endsWith("L") ? value.substring(0, value.length() - 1) : value;
        long number;
        try {
            number = Long.parseLong(digits.trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
        if (cpp) {
            if (number > 202302L) {
                return "26";
            }
            if (number >= 202302L) {
                return "23";
            }
            if (number >= 202002L) {
                return "20";
            }
            if (number >= 201703L) {
                return "17";
            }
            if (number >= 201402L) {
                return "14";
            }
            if (number >= 201103L) {
                return "11";
            }
            return "98";
        }
        // GCC 13 says 202000L for -std=c2x
        if (number >= 202000L) {
            return "23";
        }
        if (number >= 201710L) {
            return "17";
        }
        if (number >= 201112L) {
            return "11";
        }
        if (number >= 199901L) {
            return "99";
        }
        return "89";
    }
}
