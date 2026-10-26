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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class BuildOutputParserTest {
    @TempDir
    Path myRoot;

    /**
     * What {@code make --dry-run --always-make --print-directory} prints for an automake project built out of tree.
     */
    @Test
    public void testAutomakeVpathBuild() throws IOException {
        Path source = Files.createDirectories(myRoot.resolve("hello"));
        Path build = Files.createDirectories(myRoot.resolve("hello-build"));
        Files.createDirectories(source.resolve("lib"));
        Files.createDirectories(source.resolve("src"));
        Files.createDirectories(build.resolve("lib"));
        Files.createDirectories(build.resolve("src"));

        String output = String.join("\n",
            "make: Entering directory '" + build + "'",
            "make -o Makefile -o config.status  all-recursive",
            "make[1]: Entering directory '" + build + "'",
            "Making all in lib",
            "make[2]: Entering directory '" + build.resolve("lib") + "'",
            "gcc -DHAVE_CONFIG_H -I. -I../../hello/lib -I..  -DUTIL_INTERNAL=2 -I../../hello/lib   -g -O2 -MT libutil_a-util.o -MD -MP"
                + " -MF .deps/libutil_a-util.Tpo -c -o libutil_a-util.o `test -f 'util.c' || echo '../../hello/lib/'`util.c",
            "mv -f .deps/libutil_a-util.Tpo .deps/libutil_a-util.Po",
            "ar cr libutil.a libutil_a-util.o ",
            "make[2]: Leaving directory '" + build.resolve("lib") + "'",
            "Making all in src",
            "make[2]: Entering directory '" + build.resolve("src") + "'",
            "g++ -DHAVE_CONFIG_H -I. -I../../hello/src -I..  -I../../hello/lib -DHELLO=\"\\\"x y\\\"\"  -std=gnu++17 -g -O2 -MT hello-main.o"
                + " -MD -MP -MF .deps/hello-main.Tpo -c -o hello-main.o `test -f 'main.cpp' || echo '../../hello/src/'`main.cpp",
            "make[2]: Leaving directory '" + build.resolve("src") + "'",
            "make[1]: Leaving directory '" + build + "'",
            "make: Leaving directory '" + build + "'"
        );

        List<CompileCommand> commands = BuildOutputParser.parse(build, output);
        assertEquals(2, commands.size());

        CompileCommand util = commands.get(0);
        assertEquals(build.resolve("lib"), util.directory());
        assertEquals("gcc", util.compiler());
        assertEquals(source.resolve("lib/util.c"), util.file());

        CompileCommand main = commands.get(1);
        assertEquals(build.resolve("src"), main.directory());
        assertEquals(source.resolve("src/main.cpp"), main.file());

        CompilerFlags flags = CompilerFlags.parse(main.compiler(), main.arguments(), main.directory());
        assertEquals(List.of(build.resolve("src").toString(), source.resolve("src").toString(), build.toString(),
            source.resolve("lib").toString()), flags.includePaths());
        assertEquals("\"x y\"", flags.defineChanges().get("HELLO"));
        assertEquals("1", flags.defineChanges().get("HAVE_CONFIG_H"));
        assertEquals("gnu++17", flags.standard());
        assertEquals(List.of(new CompilerFlags.Source(source.resolve("src/main.cpp"), Compilers.LANGUAGE_CXX)), flags.sources());
    }

    /**
     * A source in the build directory is compiled from there - the backquoted test finds it.
     */
    @Test
    public void testAutomakeSourceInBuildDirectory() throws IOException {
        Files.writeString(myRoot.resolve("gen.c"), "");
        List<CompileCommand> commands = BuildOutputParser.parse(myRoot,
            "gcc -c -o gen.o `test -f 'gen.c' || echo '../src/'`gen.c");
        assertEquals(List.of(myRoot.resolve("gen.c")), commands.stream().map(CompileCommand::file).toList());
    }

    @Test
    public void testLibtoolAndContinuationLines() {
        String output = String.join("\n",
            "depbase=`echo foo.lo | sed 's|[^/]*$|.deps/&|;s|\\.lo$||'`;\\",
            "/bin/sh ../libtool  --tag=CC   --mode=compile gcc -DFOO -I../include \\",
            "  -c -o foo.lo foo.c &&\\",
            "mv -f $depbase.Tpo $depbase.Plo"
        );
        List<CompileCommand> commands = BuildOutputParser.parse(myRoot, output);
        assertEquals(1, commands.size());
        assertEquals("gcc", commands.get(0).compiler());
        assertEquals(myRoot.resolve("foo.c"), commands.get(0).file());
    }

    @Test
    public void testCdInLine() {
        List<CompileCommand> commands = BuildOutputParser.parse(myRoot, "cd sub && ccache cc -Iinc -c x.c; cd .. && cc -c y.c");
        assertEquals(2, commands.size());
        assertEquals(myRoot.resolve("sub/x.c"), commands.get(0).file());
        assertEquals(myRoot.resolve("y.c"), commands.get(1).file());
        assertEquals(List.of(myRoot.resolve("sub/inc").toString()),
            CompilerFlags.parse("cc", commands.get(0).arguments(), commands.get(0).directory()).includePaths());
    }

    @Test
    public void testCommandsWhichDoNotCompile() {
        String output = String.join("\n",
            "echo building",
            "gcc -o prog main.o util.o",
            "gcc -E -P -x c linker.lds.S",
            "gcc -MM deps.c",
            "rm -f gcc.c"
        );
        assertEquals(List.of(), BuildOutputParser.parse(myRoot, output));
    }

    @Test
    public void testCompileAndLink() {
        List<CompileCommand> commands = BuildOutputParser.parse(myRoot, "cc -O2 -o prog main.c util.c -lm");
        assertEquals(List.of(myRoot.resolve("main.c"), myRoot.resolve("util.c")), commands.stream().map(CompileCommand::file).toList());
    }
}
