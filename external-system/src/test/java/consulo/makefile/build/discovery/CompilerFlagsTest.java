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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class CompilerFlagsTest {
    @TempDir
    Path myRoot;

    @Test
    public void testIncludesAndDefines() {
        CompilerFlags flags = CompilerFlags.parse("cc", List.of(
            "-I", "a", "-Ib", "-iquote", "q", "-isystem", "/sys", "-isystemsys2", "-idirafter", "late",
            "-DA", "-D", "B=2", "-DC=x=y", "-UA", "-DD", "-c", "x.c"
        ), myRoot);

        assertEquals(List.of(myRoot.resolve("a").toString(), myRoot.resolve("b").toString(), myRoot.resolve("q").toString(),
            myRoot.resolve("late").toString()), flags.includePaths());
        assertEquals(List.of("/sys", myRoot.resolve("sys2").toString()), flags.systemIncludePaths());

        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("B", "2");
        expected.put("C", "x=y");
        expected.put("A", null);
        expected.put("D", "1");
        assertEquals(expected, flags.defineChanges());
        assertFalse(flags.preprocessOnly());
    }

    @Test
    public void testLanguages() {
        CompilerFlags flags = CompilerFlags.parse("gcc", List.of("a.c", "b.cpp", "C.C", "-x", "c++", "d.c", "-x", "none", "e.c", "f.s"), myRoot);
        assertEquals(List.of(
            new CompilerFlags.Source(myRoot.resolve("a.c"), Compilers.LANGUAGE_C),
            new CompilerFlags.Source(myRoot.resolve("b.cpp"), Compilers.LANGUAGE_CXX),
            new CompilerFlags.Source(myRoot.resolve("C.C"), Compilers.LANGUAGE_CXX),
            new CompilerFlags.Source(myRoot.resolve("d.c"), Compilers.LANGUAGE_CXX),
            new CompilerFlags.Source(myRoot.resolve("e.c"), Compilers.LANGUAGE_C)
        ), flags.sources());

        // g++ compiles .c as C++
        assertEquals(Compilers.LANGUAGE_CXX, CompilerFlags.parse("/usr/bin/g++-13", List.of("x.c"), myRoot).sources().get(0).language());
    }

    @Test
    public void testOptionArgumentsAreNotSources() {
        CompilerFlags flags = CompilerFlags.parse("cc", List.of("-include", "pre.h", "-MF", "x.d", "-o", "x.o", "-c", "x.c"), myRoot);
        assertEquals(List.of(myRoot.resolve("x.c")), flags.sources().stream().map(CompilerFlags.Source::file).toList());
    }

    @Test
    public void testProbeArguments() {
        CompilerFlags flags = CompilerFlags.parse("clang", List.of(
            "-std=c11", "-O2", "-m32", "-march=native", "-fno-exceptions", "-fPIC", "-Wall", "-g", "-fdiagnostics-color",
            "--target", "arm-none-eabi", "-c", "x.c"
        ), myRoot);
        assertEquals(List.of("-std=c11", "-O2", "-m32", "-march=native", "-fno-exceptions", "-fPIC", "--target=arm-none-eabi"),
            flags.probeArguments());
        assertEquals("c11", flags.standard());
        assertEquals("arm-none-eabi", flags.target());
    }

    @Test
    public void testResponseFile() throws IOException {
        Files.writeString(myRoot.resolve("args.rsp"), "-I inc\n-DX=\"a b\"\n");
        CompilerFlags flags = CompilerFlags.parse("gcc", List.of("@args.rsp", "-c", "x.c"), myRoot);
        assertEquals(List.of(myRoot.resolve("inc").toString()), flags.includePaths());
        assertEquals("a b", flags.defineChanges().get("X"));
    }

    @Test
    public void testPreprocessOnly() {
        assertTrue(CompilerFlags.parse("gcc", List.of("-E", "x.c"), myRoot).preprocessOnly());
    }

    @Test
    public void testCompilers() {
        assertTrue(Compilers.isCompiler("gcc"));
        assertTrue(Compilers.isCompiler("/opt/arm/bin/arm-none-eabi-gcc"));
        assertTrue(Compilers.isCompiler("x86_64-w64-mingw32-g++"));
        assertTrue(Compilers.isCompiler("clang++-18"));
        assertTrue(Compilers.isCompiler("cc"));
        assertFalse(Compilers.isCompiler("ccache"));
        assertFalse(Compilers.isCompiler("libtool"));
        assertFalse(Compilers.isCompiler("gcc.c"));
        assertFalse(Compilers.isCompiler("--tag=CC"));
    }

    @Test
    public void testStandards() {
        assertEquals("11", CompilerStandards.standard(Compilers.LANGUAGE_C, "gnu11", null));
        assertEquals("99", CompilerStandards.standard(Compilers.LANGUAGE_C, "iso9899:1999", null));
        assertEquals("20", CompilerStandards.standard(Compilers.LANGUAGE_CXX, "c++2a", null));
        assertNull(CompilerStandards.standard(Compilers.LANGUAGE_C, null, null));
        // the compiler's default, from what it predefines
        assertEquals("17", CompilerStandards.standard(Compilers.LANGUAGE_CXX, null, "201703L"));
        assertEquals("17", CompilerStandards.standard(Compilers.LANGUAGE_C, null, "201710L"));
        assertEquals("23", CompilerStandards.standard(Compilers.LANGUAGE_C, null, "202311L"));
        // a C standard means nothing to C++
        assertEquals("14", CompilerStandards.standard(Compilers.LANGUAGE_CXX, "c11", "201402L"));
    }
}
