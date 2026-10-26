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
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads a shell command line the way {@code /bin/sh} would, as far as a printed build command needs: statements,
 * words, quotes and escapes. Nothing is expanded except the backquoted {@code test -f} automake puts before a source
 * file of a VPATH build.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class ShellWords {
    /**
     * {@code `test -f 'util.c' || echo '../src/'`util.c} - the source is in the build directory, or else in the
     * source directory.
     */
    private static final Pattern AUTOMAKE_VPATH_TEST = Pattern.compile("test -f '([^']*)' \\|\\| echo '([^']*)'");

    private ShellWords() {
    }

    /**
     * Splits a command line into statements at {@code ;}, {@code &&}, {@code ||}, {@code |}, {@code &} and newlines, and
     * at the parentheses of a subshell - outside quotes, backquotes and {@code $(...)}.
     */
    public static List<String> splitStatements(String line) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char quote = 0;
        int substitutionDepth = 0;
        boolean backquote = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != 0) {
                current.append(c);
                if (c == '\\' && quote == '"' && i + 1 < line.length()) {
                    current.append(line.charAt(++i));
                }
                else if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '\\' && i + 1 < line.length()) {
                current.append(c).append(line.charAt(++i));
                continue;
            }
            if (c == '\'' || c == '"') {
                quote = c;
                current.append(c);
                continue;
            }
            if (c == '`') {
                backquote = !backquote;
                current.append(c);
                continue;
            }
            if (c == '$' && i + 1 < line.length() && line.charAt(i + 1) == '(') {
                substitutionDepth++;
                current.append("$(");
                i++;
                continue;
            }
            if (backquote || substitutionDepth > 0) {
                if (c == ')' && !backquote) {
                    substitutionDepth--;
                }
                current.append(c);
                continue;
            }
            if (c == ';' || c == '|' || c == '&' || c == '\n' || c == '(' || c == ')') {
                addStatement(statements, current);
                // && and || are one separator
                if ((c == '|' || c == '&') && i + 1 < line.length() && line.charAt(i + 1) == c) {
                    i++;
                }
                continue;
            }
            current.append(c);
        }
        addStatement(statements, current);
        return statements;
    }

    private static void addStatement(List<String> statements, StringBuilder current) {
        String statement = current.toString().trim();
        if (!statement.isEmpty()) {
            statements.add(statement);
        }
        current.setLength(0);
    }

    /**
     * Splits a statement into words: whitespace separates, quotes group, a backslash escapes the next character.
     *
     * @param backquoteValue what a backquoted command expands to, {@code null} to keep its text
     */
    public static List<String> splitWords(String statement, Function<String, @Nullable String> backquoteValue) {
        List<String> words = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inWord = false;

        for (int i = 0; i < statement.length(); i++) {
            char c = statement.charAt(i);
            if (c == '\'') {
                int end = statement.indexOf('\'', i + 1);
                end = end < 0 ? statement.length() : end;
                current.append(statement, i + 1, end);
                i = end;
                inWord = true;
            }
            else if (c == '"') {
                i++;
                for (; i < statement.length() && statement.charAt(i) != '"'; i++) {
                    char d = statement.charAt(i);
                    if (d == '\\' && i + 1 < statement.length() && "\"\\$`".indexOf(statement.charAt(i + 1)) >= 0) {
                        d = statement.charAt(++i);
                    }
                    current.append(d);
                }
                inWord = true;
            }
            else if (c == '`') {
                int end = statement.indexOf('`', i + 1);
                end = end < 0 ? statement.length() : end;
                String command = statement.substring(i + 1, end);
                String value = backquoteValue.apply(command);
                current.append(value != null ? value : "`" + command + "`");
                i = end;
                inWord = true;
            }
            else if (c == '\\' && i + 1 < statement.length()) {
                current.append(statement.charAt(++i));
                inWord = true;
            }
            else if (Character.isWhitespace(c)) {
                if (inWord) {
                    words.add(current.toString());
                    current.setLength(0);
                    inWord = false;
                }
            }
            else {
                current.append(c);
                inWord = true;
            }
        }
        if (inWord) {
            words.add(current.toString());
        }
        return words;
    }

    /**
     * The value of automake's {@code `test -f 'X' || echo 'DIR/'`}: empty when {@code X} exists, {@code DIR/} otherwise;
     * {@code null} for any other command.
     */
    public static @Nullable String automakeVpathTest(String command, Function<String, Boolean> fileExists) {
        Matcher matcher = AUTOMAKE_VPATH_TEST.matcher(command.trim());
        if (!matcher.matches()) {
            return null;
        }
        return fileExists.apply(matcher.group(1)) ? "" : matcher.group(2);
    }
}
