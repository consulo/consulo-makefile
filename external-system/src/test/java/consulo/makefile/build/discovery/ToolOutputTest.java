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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What make and the compiler print, recorded from GNU Make 4.4 and GCC 16.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public class ToolOutputTest {
    @TempDir
    Path myRoot;

    @Test
    public void testGccProbe() throws IOException {
        CompilerBuiltins builtins = CompilerBuiltins.parse(resource("gcc-probe.stdout"), resource("gcc-probe.stderr"));
        assertEquals("x86_64-pc-linux-gnu", builtins.target());
        assertEquals("201703L", builtins.versionMacro());
        assertEquals(List.of(
            "/usr/lib/gcc/x86_64-pc-linux-gnu/16/../../../../include/c++/16",
            "/usr/lib/gcc/x86_64-pc-linux-gnu/16/../../../../include/c++/16/x86_64-pc-linux-gnu",
            "/usr/lib/gcc/x86_64-pc-linux-gnu/16/../../../../include/c++/16/backward",
            "/usr/lib/gcc/x86_64-pc-linux-gnu/16/include",
            "/usr/local/include",
            "/usr/include"
        ), builtins.systemIncludePaths());
        assertEquals("16", builtins.macros().get("__GNUC__"));
        assertEquals("1", builtins.macros().get("__x86_64__"));
        // the language version predefines these
        assertFalse(builtins.macros().containsKey("__cplusplus"));
        assertFalse(builtins.macros().containsKey("__STDC__"));
        // function-like macros do not fit
        assertFalse(builtins.macros().keySet().stream().anyMatch(name -> name.contains("(")));
    }

    @Test
    public void testAutomakeDatabase() throws IOException {
        MakeDatabase database = MakeDatabase.parse(resource("automake-database.txt"));
        assertEquals("all", database.defaultGoal());
        assertEquals("all", database.targets().get(0));
        assertTrue(database.targets().containsAll(List.of("check", "clean", "distclean", "install", "dist", "distcheck")),
            database.targets().toString());
        for (String target : database.targets()) {
            assertFalse(target.endsWith("-am") || target.endsWith("-recursive") || target.startsWith("am--") || target.contains("."),
                target);
        }
    }

    @Test
    public void testPlainMakefileDatabase() {
        String output = String.join("\n",
            "# GNU Make 4.4.1",
            ".DEFAULT_GOAL := prog",
            "# Files",
            "",
            "prog: main.o util.o",
            "#  Last modified 2026-10-25",
            "\tcc -o prog main.o util.o",
            "",
            "# Not a target:",
            "main.c:",
            "",
            "main.o: main.c util.h",
            "",
            "test: prog",
            "#  Phony target (prerequisite of .PHONY).",
            "",
            ".PHONY: test clean",
            "",
            "clean:",
            "",
            "# Finished Make data base on Sun Oct 25 2026"
        );
        MakeDatabase database = MakeDatabase.parse(output);
        assertEquals("prog", database.defaultGoal());
        assertEquals(List.of("prog", "test", "clean"), database.targets());
    }

    /**
     * OpenJDK has thousands of targets; its entry points are offered before the variants of each module.
     */
    @Test
    public void testManyTargets() {
        StringBuilder output = new StringBuilder(".DEFAULT_GOAL := default\n# Files\n");
        for (int i = 0; i < 300; i++) {
            output.append("clean-java.module").append(i).append("-native:\n\n");
        }
        output.append("images:\n\nhotspot:\n\ntest:\n\ndefault:\n\n");
        output.append(".PHONY: images hotspot test default\n");
        for (int i = 0; i < 300; i++) {
            output.append(" clean-java.module").append(i).append("-native");
        }
        output.append("\n# Finished Make data base\n");

        MakeDatabase database = MakeDatabase.parse(output.toString());
        assertEquals("default", database.targets().get(0));
        assertTrue(database.targets().containsAll(List.of("images", "hotspot", "test")), database.targets().toString());
        assertTrue(database.hasTarget("clean-java.module299-native"));
    }

    @Test
    public void testCompilationDatabase() throws IOException {
        Path json = myRoot.resolve(CompilationDatabase.FILE_NAME);
        Files.writeString(json, """
            [
              {"directory": "%1$s/build", "file": "../src/a.c", "command": "/usr/bin/cc -I../include -DX=\\"a b\\" -c ../src/a.c -o a.o"},
              {"directory": "%1$s/build", "file": "%1$s/src/b.cpp", "arguments": ["ccache", "c++", "-std=c++20", "-c", "%1$s/src/b.cpp"]},
              {"directory": "%1$s/build", "file": "c.s", "command": "as c.s"}
            ]
            """.formatted(myRoot));

        List<CompileCommand> commands = CompilationDatabase.read(json);
        assertEquals(2, commands.size());
        assertEquals(myRoot.resolve("src/a.c"), commands.get(0).file());
        CompilerFlags flags = CompilerFlags.parse(commands.get(0).compiler(), commands.get(0).arguments(), commands.get(0).directory());
        assertEquals(List.of(myRoot.resolve("include").toString()), flags.includePaths());
        // the shell takes the quotes away
        assertEquals("a b", flags.defineChanges().get("X"));

        assertEquals("c++", commands.get(1).compiler());
        assertEquals(myRoot.resolve("src/b.cpp"), commands.get(1).file());
    }

    private static String resource(String name) throws IOException {
        try (InputStream stream = ToolOutputTest.class.getResourceAsStream("/discovery/" + name)) {
            assertNotNull(stream, name);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
