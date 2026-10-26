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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a JSON compilation database - {@code compile_commands.json}, which CMake, Meson, Bear and compiledb write.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class CompilationDatabase {
    public static final String FILE_NAME = "compile_commands.json";

    private CompilationDatabase() {
    }

    public static List<CompileCommand> read(Path file) throws IOException {
        JsonElement root;
        try (Reader reader = Files.newBufferedReader(file)) {
            root = JsonParser.parseReader(reader);
        }
        catch (JsonParseException e) {
            throw new IOException(e.getMessage(), e);
        }
        if (!root.isJsonArray()) {
            throw new IOException(FILE_NAME + " is not an array");
        }

        Path parent = file.toAbsolutePath().getParent();
        Path baseDirectory = parent != null ? parent : Path.of("");
        List<CompileCommand> commands = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray()) {
            if (element.isJsonObject()) {
                commands.addAll(commandsOf(baseDirectory, element.getAsJsonObject()));
            }
        }
        return commands;
    }

    private static List<CompileCommand> commandsOf(Path baseDirectory, JsonObject entry) {
        String directoryText = string(entry, "directory");
        Path directory = directoryText == null ? baseDirectory : baseDirectory.resolve(directoryText).normalize();

        // "arguments" is the command split already; "command" is one shell-escaped string
        List<String> words = new ArrayList<>();
        JsonElement arguments = entry.get("arguments");
        if (arguments != null && arguments.isJsonArray()) {
            JsonArray array = arguments.getAsJsonArray();
            for (JsonElement argument : array) {
                words.add(argument.getAsString());
            }
        }
        else {
            String command = string(entry, "command");
            if (command == null) {
                return List.of();
            }
            words.addAll(ShellWords.splitWords(command, value -> null));
        }

        List<CompileCommand> commands = BuildOutputParser.commandsOf(directory, words, 0);
        String fileText = string(entry, "file");
        if (fileText == null) {
            return commands;
        }
        // an entry is about its file; the command could compile others too
        Path file = directory.resolve(fileText).normalize();
        List<CompileCommand> ofFile = new ArrayList<>();
        for (CompileCommand command : commands) {
            if (command.file().equals(file)) {
                ofFile.add(command);
            }
        }
        return ofFile.isEmpty() ? commands : ofFile;
    }

    private static @Nullable String string(JsonObject object, String name) {
        JsonElement element = object.get(name);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }
}
