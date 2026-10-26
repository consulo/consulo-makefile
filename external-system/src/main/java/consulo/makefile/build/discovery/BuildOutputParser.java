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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collects the compiler invocations from what {@code make --dry-run --print-directory} prints: the commands, and the
 * {@code Entering directory} lines which say where the commands of a recursive make run.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class BuildOutputParser {
    /**
     * {@code make[2]: Entering directory '/src/lib'} - GNU make quotes with {@code `'} before 4.0, with {@code ''} since,
     * and with the locale's quotes unless {@code LC_ALL=C}.
     */
    private static final Pattern DIRECTORY_CHANGE = Pattern.compile(
        "\\S*?make(?:\\.exe)?(?:\\[\\d+])?: (Entering|Leaving) directory [`'‘\"](.*)['’\"]\\s*"
    );

    /**
     * {@code NAME=value} before a command sets its environment.
     */
    private static final Pattern ASSIGNMENT = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*=.*");

    private final Path myBaseDirectory;
    private final Deque<Path> myDirectories = new ArrayDeque<>();
    private final List<CompileCommand> myCommands = new ArrayList<>();
    private final StringBuilder myContinuation = new StringBuilder();

    public BuildOutputParser(Path baseDirectory) {
        myBaseDirectory = baseDirectory;
    }

    public static List<CompileCommand> parse(Path baseDirectory, String output) {
        BuildOutputParser parser = new BuildOutputParser(baseDirectory);
        for (String line : output.split("\\r?\\n")) {
            parser.accept(line);
        }
        parser.finish();
        return parser.getCommands();
    }

    public void accept(String line) {
        // a recipe line ending with a backslash goes on in the next one
        if (line.endsWith("\\")) {
            myContinuation.append(line, 0, line.length() - 1).append(' ');
            return;
        }
        if (myContinuation.isEmpty()) {
            acceptLogicalLine(line);
            return;
        }
        myContinuation.append(line);
        String logicalLine = myContinuation.toString();
        myContinuation.setLength(0);
        acceptLogicalLine(logicalLine);
    }

    public void finish() {
        if (!myContinuation.isEmpty()) {
            String line = myContinuation.toString();
            myContinuation.setLength(0);
            acceptLogicalLine(line);
        }
    }

    public List<CompileCommand> getCommands() {
        return List.copyOf(myCommands);
    }

    private Path currentDirectory() {
        Path directory = myDirectories.peek();
        return directory != null ? directory : myBaseDirectory;
    }

    private void acceptLogicalLine(String line) {
        Matcher matcher = DIRECTORY_CHANGE.matcher(line);
        if (matcher.matches()) {
            if ("Entering".equals(matcher.group(1))) {
                myDirectories.push(currentDirectory().resolve(matcher.group(2)).normalize());
            }
            else if (!myDirectories.isEmpty()) {
                myDirectories.pop();
            }
            return;
        }

        // a cd changes the directory for the rest of the line only - each line runs in a shell of its own
        Path directory = currentDirectory();
        for (String statement : ShellWords.splitStatements(line)) {
            Path statementDirectory = directory;
            List<String> words = ShellWords.splitWords(statement,
                command -> ShellWords.automakeVpathTest(command, file -> Files.exists(statementDirectory.resolve(file))));
            int start = 0;
            while (start < words.size() && ASSIGNMENT.matcher(words.get(start)).matches()) {
                start++;
            }
            if (start >= words.size()) {
                continue;
            }
            if (words.get(start).equals("cd")) {
                Path target = start + 1 < words.size() ? resolveDirectory(directory, words.get(start + 1)) : null;
                if (target != null) {
                    directory = target;
                }
                continue;
            }
            myCommands.addAll(commandsOf(directory, words, start));
        }
    }

    private static @Nullable Path resolveDirectory(Path directory, String argument) {
        if (argument.isEmpty() || argument.contains("$") || argument.contains("`") || argument.equals("-")) {
            return null;
        }
        return directory.resolve(argument).normalize();
    }

    /**
     * The compile commands of the words of one shell statement: none when it does not run a compiler, or only
     * preprocesses.
     */
    static List<CompileCommand> commandsOf(Path directory, List<String> words, int start) {
        // the compiler may come after a wrapper: ccache gcc, libtool --mode=compile gcc, /bin/sh ../libtool ...
        int compilerIndex = -1;
        for (int i = start; i < words.size(); i++) {
            String word = words.get(i);
            if (word.startsWith("-") && !word.startsWith("--")) {
                break;
            }
            if (Compilers.isCompiler(word)) {
                compilerIndex = i;
                break;
            }
        }
        if (compilerIndex < 0) {
            return List.of();
        }

        String compiler = words.get(compilerIndex);
        List<String> arguments = List.copyOf(words.subList(compilerIndex + 1, words.size()));
        CompilerFlags flags = CompilerFlags.parse(compiler, arguments, directory);
        if (flags.preprocessOnly()) {
            return List.of();
        }
        List<CompileCommand> commands = new ArrayList<>();
        for (CompilerFlags.Source source : flags.sources()) {
            commands.add(new CompileCommand(directory, compiler, arguments, source.file()));
        }
        return commands;
    }
}
