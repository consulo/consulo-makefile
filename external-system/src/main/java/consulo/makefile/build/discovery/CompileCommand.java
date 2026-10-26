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

import java.nio.file.Path;
import java.util.List;

/**
 * One compiler invocation of the build.
 *
 * @param directory the working directory of the compiler
 * @param compiler  the compiler as the command names it - {@code gcc}, {@code /usr/bin/clang++}
 * @param arguments the arguments after the compiler
 * @param file      the source file, absolute
 * @author VISTALL
 * @since 2026-10-25
 */
public record CompileCommand(Path directory, String compiler, List<String> arguments, Path file) {
}
