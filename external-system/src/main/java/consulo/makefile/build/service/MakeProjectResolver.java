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
package consulo.makefile.build.service;

import consulo.makefile.build.MakeConstants;
import consulo.makefile.build.MakeProjectPaths;
import consulo.makefile.build.discovery.BuildOutputParser;
import consulo.makefile.build.discovery.CompilationDatabase;
import consulo.makefile.build.discovery.CompileCommand;
import consulo.makefile.build.discovery.CompilerBuiltins;
import consulo.makefile.build.discovery.CompilerFlags;
import consulo.makefile.build.discovery.CompilerStandards;
import consulo.makefile.build.discovery.MakeDatabase;
import consulo.makefile.build.discovery.ShellWords;
import consulo.makefile.build.localize.MakeBuildLocalize;
import consulo.makefile.build.setting.MakeExecutionSettings;
import consulo.externalSystem.model.DataNode;
import consulo.externalSystem.model.ProjectKeys;
import consulo.externalSystem.model.project.ContentRootData;
import consulo.externalSystem.model.project.LibraryData;
import consulo.externalSystem.model.project.LibraryDependencyData;
import consulo.externalSystem.model.project.LibraryLevel;
import consulo.externalSystem.model.project.LibraryPathType;
import consulo.externalSystem.model.project.ModuleData;
import consulo.externalSystem.model.task.ExternalSystemTaskId;
import consulo.externalSystem.model.task.ExternalSystemTaskNotificationEvent;
import consulo.externalSystem.model.task.ExternalSystemTaskNotificationListener;
import consulo.externalSystem.model.task.TaskData;
import consulo.externalSystem.rt.model.ExternalSystemException;
import consulo.externalSystem.rt.model.ExternalSystemSourceType;
import consulo.externalSystem.service.project.ExternalSystemProjectResolver;
import consulo.externalSystem.service.project.ProjectData;
import consulo.externalSystem.util.ExternalSystemApiUtil;
import consulo.logging.Logger;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.util.CapturingProcessUtil;
import consulo.process.util.ProcessOutput;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves a Make project without evaluating its makefiles: make itself prints what it would run
 * ({@code --dry-run --always-make}), and the compiler commands of that tell the include directories, defines and
 * standards. A project without a makefile is configured first - autoconf's {@code configure}, generated when the
 * project has only {@code configure.ac} - or read from its {@code compile_commands.json}.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeProjectResolver implements ExternalSystemProjectResolver<MakeExecutionSettings> {
    private static final Logger LOG = Logger.getInstance(MakeProjectResolver.class);

    private static final int PROBE_TIMEOUT_MS = 30_000;

    /**
     * The files a dry run must not remake: GNU make runs the rule of an out of date makefile even with
     * {@code --dry-run}, and with {@code --always-make} every makefile is - so automake's would run configure again.
     */
    /**
     * The target of an OpenJDK makefile which writes the compilation database of its configuration.
     */
    private static final String COMPILE_COMMANDS_TARGET = "compile-commands";

    private static final List<String> OLD_FILES = List.of("GNUmakefile", "makefile", "Makefile", "config.status");

    private final MakeProcessRunner myRunner = new MakeProcessRunner();

    @Override
    public @Nullable DataNode<ProjectData> resolveProjectInfo(ExternalSystemTaskId id,
                                                              String projectPath,
                                                              boolean isPreviewMode,
                                                              @Nullable MakeExecutionSettings settings,
                                                              ExternalSystemTaskNotificationListener listener)
        throws ExternalSystemException, IllegalArgumentException, IllegalStateException {
        Path sourceDir = MakeProjectPaths.getSourceDir(projectPath);
        Path buildDir = MakeProjectPaths.getBuildDir(sourceDir, settings != null ? settings.getBuildDirectory() : null);
        List<String> makeArguments = settings == null ? List.of() : ShellWords.splitWords(settings.getMakeArguments(), command -> null);
        List<String> configureArguments =
            settings == null ? List.of() : ShellWords.splitWords(settings.getConfigureArguments(), command -> null);

        String projectName = projectName(sourceDir);
        String sourcePath = sourceDir.toString();
        ProjectData projectData = new ProjectData(MakeConstants.SYSTEM_ID, projectName, sourcePath, projectPath);
        DataNode<ProjectData> projectNode = new DataNode<>(ProjectKeys.PROJECT, projectData, null);
        ModuleData moduleData = new ModuleData(projectName, MakeConstants.SYSTEM_ID, projectName, sourcePath, projectPath);
        DataNode<ModuleData> moduleNode = projectNode.createChild(ProjectKeys.MODULE, moduleData);
        moduleNode.createChild(ProjectKeys.CONTENT_ROOT, createContentRoot(sourceDir));

        if (isPreviewMode) {
            return projectNode;
        }

        Path makefile = MakeProjectPaths.findMakefile(buildDir);
        boolean configured = false;
        if (makefile == null && findCompilationDatabase(buildDir, sourceDir) == null) {
            configured = configure(id, listener, sourceDir, buildDir, configureArguments);
            makefile = MakeProjectPaths.findMakefile(buildDir);
        }

        List<CompileCommand> commands = List.of();
        MakeDatabase makeDatabase = MakeDatabase.EMPTY;
        if (makefile != null) {
            makeDatabase = readTargets(id, listener, buildDir, makeArguments);
            commands = dryRun(id, listener, buildDir, makeArguments);
            // a makefile which needs configure first - OpenJDK's only says "please run configure"
            if (commands.isEmpty() && !configured && canConfigure(sourceDir)
                && configure(id, listener, sourceDir, buildDir, configureArguments)) {
                makeDatabase = readTargets(id, listener, buildDir, makeArguments);
                commands = dryRun(id, listener, buildDir, makeArguments);
            }
            // the project knows how to write its compilation database - a staged build, like OpenJDK's, which a dry
            // run cannot see past its first stage
            if (makeDatabase.hasTarget(COMPILE_COMMANDS_TARGET)) {
                notify(listener, id, MakeBuildLocalize.statusGeneratingCompilationDatabase().get());
                GeneralCommandLine commandLine = makeCommandLine(buildDir, makeArguments);
                commandLine.addParameter(COMPILE_COMMANDS_TARGET);
                runChecked(id, listener, commandLine, null, true);
            }
        }

        Path database = findCompilationDatabase(buildDir, sourceDir);
        if (makefile == null && database == null) {
            throw new ExternalSystemException(MakeBuildLocalize.errorNoBuildFiles(sourcePath).get());
        }
        if (database != null) {
            notify(listener, id, MakeBuildLocalize.statusReadingCompilationDatabase(database.toString()).get());
            List<CompileCommand> fromDatabase;
            try {
                fromDatabase = CompilationDatabase.read(database);
            }
            catch (IOException e) {
                throw new ExternalSystemException(MakeBuildLocalize.errorFailedReadFile(database.toString(), String.valueOf(e.getMessage())).get());
            }
            // what make says now is fresher than a database written once; a database which knows more files is one the
            // project writes because make cannot tell
            if (fromDatabase.size() > commands.size()) {
                commands = fromDatabase;
            }
        }

        for (MakeCompileData compileData : compileData(id, listener, commands)) {
            moduleNode.createChild(MakeCompileData.KEY, compileData);
        }
        addHeaderSearchPathsLibrary(projectNode, moduleNode, projectName, sourceDir);

        for (String target : makeDatabase.targets()) {
            String description = target.equals(makeDatabase.defaultGoal())
                ? MakeBuildLocalize.taskBuildDefault().get()
                : MakeBuildLocalize.taskBuildTarget(target).get();
            projectNode.createChild(ProjectKeys.TASK, new TaskData(MakeConstants.SYSTEM_ID, target, projectPath, description));
        }
        return projectNode;
    }

    private static String projectName(Path sourceDir) {
        Path fileName = sourceDir.getFileName();
        return fileName != null ? fileName.toString() : sourceDir.toString();
    }

    private static ContentRootData createContentRoot(Path sourceDir) {
        ContentRootData contentRoot = new ContentRootData(MakeConstants.SYSTEM_ID, sourceDir.toString());
        // a build directory inside stays in: configure generates headers there, config.h, which the sources include
        contentRoot.storePath(ExternalSystemSourceType.SOURCE, sourceDir.toString());
        return contentRoot;
    }

    /**
     * The compilation database of the build directory or of the sources - or the newest of the configurations under
     * {@code build/}, where OpenJDK writes one per configuration.
     */
    private static @Nullable Path findCompilationDatabase(Path buildDir, Path sourceDir) {
        for (Path directory : List.of(buildDir, sourceDir)) {
            Path database = directory.resolve(CompilationDatabase.FILE_NAME);
            if (Files.isRegularFile(database)) {
                return database;
            }
        }
        Path newest = null;
        long newestTime = Long.MIN_VALUE;
        try (DirectoryStream<Path> configurations = Files.newDirectoryStream(sourceDir.resolve("build"), Files::isDirectory)) {
            for (Path configuration : configurations) {
                Path database = configuration.resolve(CompilationDatabase.FILE_NAME);
                if (Files.isRegularFile(database)) {
                    long time = Files.getLastModifiedTime(database).toMillis();
                    if (time > newestTime) {
                        newest = database;
                        newestTime = time;
                    }
                }
            }
        }
        catch (IOException ignored) {
            // no build directory
        }
        return newest;
    }

    // -------------------------------------------------------------------------
    // configure
    // -------------------------------------------------------------------------

    /**
     * Makes the makefiles of a project which has none yet: generates its configure script when it has only what autoconf
     * makes one from, then runs it in the build directory.
     */
    private boolean configure(ExternalSystemTaskId id,
                           ExternalSystemTaskNotificationListener listener,
                           Path sourceDir,
                           Path buildDir,
                           List<String> configureArguments) throws ExternalSystemException {
        Path configure = sourceDir.resolve(MakeConstants.CONFIGURE);
        if (!Files.isRegularFile(configure)) {
            GeneralCommandLine generate = null;
            Path autogen = sourceDir.resolve(MakeConstants.AUTOGEN);
            if (Files.isRegularFile(autogen)) {
                // the convention of autogen.sh scripts which run configure themselves
                generate = scriptCommandLine(autogen).withEnvironment("NOCONFIGURE", "1");
            }
            else if (hasConfigureSource(sourceDir)) {
                generate = new GeneralCommandLine("autoreconf", "--install", "--force");
            }
            if (generate == null) {
                return false;
            }
            notify(listener, id, MakeBuildLocalize.statusGeneratingConfigure().get());
            runChecked(id, listener, generate.withWorkDirectory(sourceDir.toFile()), null, true);
        }
        if (!Files.isRegularFile(configure)) {
            return false;
        }

        try {
            Files.createDirectories(buildDir);
        }
        catch (IOException e) {
            throw new ExternalSystemException(MakeBuildLocalize.errorCannotCreateBuildDir(String.valueOf(e.getMessage())).get());
        }
        notify(listener, id, MakeBuildLocalize.statusConfiguring().get());
        GeneralCommandLine commandLine = scriptCommandLine(configure);
        commandLine.addParameters(configureArguments);
        runChecked(id, listener, commandLine.withWorkDirectory(buildDir.toFile()), null, true);
        return true;
    }

    private static boolean canConfigure(Path sourceDir) {
        return Files.isRegularFile(sourceDir.resolve(MakeConstants.CONFIGURE))
            || Files.isRegularFile(sourceDir.resolve(MakeConstants.AUTOGEN))
            || hasConfigureSource(sourceDir);
    }

    /**
     * Runs a script with the interpreter its first line names - OpenJDK's configure is a bash script, which a POSIX
     * {@code sh} cannot run; one which names none, with {@code sh}.
     */
    private static GeneralCommandLine scriptCommandLine(Path script) {
        if (Files.isExecutable(script)) {
            try (BufferedReader reader = Files.newBufferedReader(script)) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.startsWith("#!")) {
                    return new GeneralCommandLine(script.toString());
                }
            }
            catch (IOException ignored) {
                // read as a shell script below
            }
        }
        return new GeneralCommandLine("sh", script.toString());
    }

    private static boolean hasConfigureSource(Path sourceDir) {
        for (String name : MakeConstants.CONFIGURE_SOURCES) {
            if (Files.isRegularFile(sourceDir.resolve(name))) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // make
    // -------------------------------------------------------------------------

    /**
     * What a full build would compile: {@code make --dry-run --always-make --keep-going --print-directory}. Recursive
     * makes run even so - a recipe line with {@code $(MAKE)} does - and print their commands, under the directory they
     * run in.
     */
    private List<CompileCommand> dryRun(ExternalSystemTaskId id,
                                        ExternalSystemTaskNotificationListener listener,
                                        Path buildDir,
                                        List<String> makeArguments) throws ExternalSystemException {
        notify(listener, id, MakeBuildLocalize.statusCollectingCommands().get());
        GeneralCommandLine commandLine = makeCommandLine(buildDir, makeArguments);
        commandLine.addParameters("--dry-run", "--always-make", "--keep-going", "--print-directory");
        addOldFiles(commandLine);
        // the -o of the options above is not passed to a recursive make; through $(MAKE) it is
        StringBuilder recursiveMake = new StringBuilder(MakeConstants.MAKE_EXECUTABLE);
        for (String file : OLD_FILES) {
            recursiveMake.append(" -o ").append(file);
        }
        commandLine.addParameter("MAKE=" + recursiveMake);

        StringBuilder stdout = new StringBuilder();
        // --keep-going goes on past the targets it cannot make, and fails in the end; what it printed is still right
        run(id, listener, commandLine, stdout, false);
        return BuildOutputParser.parse(buildDir, stdout.toString());
    }

    /**
     * The targets of the makefile, from make's data base - printed without building anything ({@code --question}).
     */
    private MakeDatabase readTargets(ExternalSystemTaskId id,
                                     ExternalSystemTaskNotificationListener listener,
                                     Path buildDir,
                                     List<String> makeArguments) throws ExternalSystemException {
        notify(listener, id, MakeBuildLocalize.statusReadingTargets().get());
        GeneralCommandLine commandLine = makeCommandLine(buildDir, makeArguments);
        commandLine.addParameters("--print-data-base", "--dry-run", "--question", "--no-builtin-rules");
        addOldFiles(commandLine);
        // a target no makefile has, so make has nothing to do after reading them
        commandLine.addParameter(".DEFAULT");

        StringBuilder stdout = new StringBuilder();
        run(id, listener, commandLine, stdout, false);
        return MakeDatabase.parse(stdout.toString());
    }

    static GeneralCommandLine makeCommandLine(Path buildDir, List<String> makeArguments) {
        GeneralCommandLine commandLine = new GeneralCommandLine(MakeConstants.MAKE_EXECUTABLE);
        commandLine.addParameters(makeArguments);
        commandLine.setWorkDirectory(buildDir.toString());
        // make says "Entering directory" in the words and quotes of the locale otherwise
        commandLine.withEnvironment("LC_ALL", "C");
        return commandLine;
    }

    private static void addOldFiles(GeneralCommandLine commandLine) {
        for (String file : OLD_FILES) {
            commandLine.addParameters("-o", file);
        }
    }

    // -------------------------------------------------------------------------
    // compile settings
    // -------------------------------------------------------------------------

    private record ProbeKey(String compiler, String language, List<String> arguments) {
    }

    private record GroupKey(String language,
                            List<String> includePaths,
                            List<String> systemIncludePaths,
                            Map<String, String> builtinDefines,
                            Map<String, String> defines,
                            @Nullable String standard,
                            @Nullable String target) {
    }

    /**
     * The compile settings of the commands, one per group of commands which agree on all of them - with what the
     * compiler predefines, asked once per compiler, language and the flags which change it.
     */
    private List<MakeCompileData> compileData(ExternalSystemTaskId id,
                                              ExternalSystemTaskNotificationListener listener,
                                              List<CompileCommand> commands) throws ExternalSystemException {
        Map<ProbeKey, CompilerBuiltins> builtinsByProbe = new HashMap<>();
        Set<GroupKey> groups = new LinkedHashSet<>();

        Path emptyInput;
        try {
            emptyInput = Files.createTempFile("consulo-make-probe", ".txt");
        }
        catch (IOException e) {
            throw new ExternalSystemException(String.valueOf(e.getMessage()));
        }
        try {
            for (CompileCommand command : commands) {
                if (myRunner.isCancelled()) {
                    throw new ExternalSystemException(MakeBuildLocalize.errorCancelled().get());
                }
                CompilerFlags flags = CompilerFlags.parse(command.compiler(), command.arguments(), command.directory());
                String language = languageOf(flags, command.file());
                if (language == null) {
                    continue;
                }

                String compiler = command.compiler().contains("/")
                    ? command.directory().resolve(command.compiler()).normalize().toString()
                    : command.compiler();
                ProbeKey probeKey = new ProbeKey(compiler, language, flags.probeArguments());
                CompilerBuiltins builtins = builtinsByProbe.get(probeKey);
                if (builtins == null) {
                    notify(listener, id, MakeBuildLocalize.statusProbingCompiler(compiler).get());
                    builtins = probe(probeKey, command.directory(), emptyInput);
                    builtinsByProbe.put(probeKey, builtins);
                }
                groups.add(groupOf(language, flags, builtins));
            }
        }
        finally {
            try {
                Files.deleteIfExists(emptyInput);
            }
            catch (IOException ignored) {
            }
        }

        List<MakeCompileData> result = new ArrayList<>();
        for (GroupKey group : groups) {
            result.add(new MakeCompileData(group.language(), group.includePaths(), group.systemIncludePaths(), group.builtinDefines(),
                group.defines(), group.standard(), group.target()));
        }
        return result;
    }

    private static @Nullable String languageOf(CompilerFlags flags, Path file) {
        for (CompilerFlags.Source source : flags.sources()) {
            if (source.file().equals(file)) {
                return source.language();
            }
        }
        return null;
    }

    private static GroupKey groupOf(String language, CompilerFlags flags, CompilerBuiltins builtins) {
        Map<String, String> builtinDefines = new LinkedHashMap<>(builtins.macros());
        Map<String, String> defines = new LinkedHashMap<>();
        for (Map.Entry<String, @Nullable String> change : flags.defineChanges().entrySet()) {
            String value = change.getValue();
            if (value == null) {
                // -U undefines a predefined macro too
                builtinDefines.remove(change.getKey());
                defines.remove(change.getKey());
            }
            else {
                defines.put(change.getKey(), value);
            }
        }

        Set<String> systemIncludePaths = new LinkedHashSet<>(flags.systemIncludePaths());
        for (String path : builtins.systemIncludePaths()) {
            systemIncludePaths.add(Path.of(path).normalize().toString());
        }

        String target = flags.target() != null ? flags.target() : builtins.target();
        String standard = CompilerStandards.standard(language, flags.standard(), builtins.versionMacro());
        return new GroupKey(language, List.copyOf(new LinkedHashSet<>(flags.includePaths())), List.copyOf(systemIncludePaths),
            builtinDefines, defines, standard, target);
    }

    /**
     * Asks the compiler what it predefines and where its headers are; nothing, when it cannot tell - a compiler which is
     * not GCC-compatible, or not installed here.
     */
    private static CompilerBuiltins probe(ProbeKey key, Path directory, Path emptyInput) {
        GeneralCommandLine commandLine = new GeneralCommandLine(key.compiler());
        commandLine.addParameters(CompilerBuiltins.probeArguments(key.language(), key.arguments(), emptyInput.toString()));
        commandLine.setWorkDirectory(directory.toString());
        commandLine.withEnvironment("LC_ALL", "C");
        try {
            ProcessOutput output = CapturingProcessUtil.execAndGetOutput(commandLine, PROBE_TIMEOUT_MS);
            if (output.getExitCode() != 0 || output.isTimeout()) {
                LOG.info("Cannot query " + commandLine.getCommandLineString() + ": exit code " + output.getExitCode());
                return CompilerBuiltins.EMPTY;
            }
            return CompilerBuiltins.parse(output.getStdout(), output.getStderr());
        }
        catch (ExecutionException e) {
            LOG.info("Cannot query " + commandLine.getCommandLineString(), e);
            return CompilerBuiltins.EMPTY;
        }
    }

    /**
     * The include directories outside the project - the compiler's own, where the standard library headers are, and
     * those of the dependencies - become the sources of a library of the module, so they are indexed and listed under
     * the external libraries.
     */
    private static void addHeaderSearchPathsLibrary(DataNode<ProjectData> projectNode,
                                                    DataNode<ModuleData> moduleNode,
                                                    String projectName,
                                                    Path sourceDir) {
        Set<String> paths = new LinkedHashSet<>();
        for (DataNode<MakeCompileData> node : ExternalSystemApiUtil.findAll(moduleNode, MakeCompileData.KEY)) {
            MakeCompileData compileData = node.getData();
            List<String> includePaths = new ArrayList<>(compileData.getIncludePaths());
            includePaths.addAll(compileData.getSystemIncludePaths());
            for (String includePath : includePaths) {
                Path path = Path.of(includePath);
                if (!path.startsWith(sourceDir) && Files.isDirectory(path)) {
                    paths.add(includePath);
                }
            }
        }
        if (paths.isEmpty()) {
            return;
        }

        LibraryData library = new LibraryData(MakeConstants.SYSTEM_ID, "Header Search Paths (" + projectName + ")");
        for (String path : paths) {
            // headers are what a C/C++ library is compiled against; the project view lists only these roots
            library.addPath(LibraryPathType.BINARY, path);
            library.addPath(LibraryPathType.SOURCE, path);
        }
        // a project library is imported before the dependencies on it, and its roots are updated on every refresh
        projectNode.createChild(ProjectKeys.LIBRARY, library);
        moduleNode.createChild(ProjectKeys.LIBRARY_DEPENDENCY, new LibraryDependencyData(moduleNode.getData(), library, LibraryLevel.PROJECT));
    }

    // -------------------------------------------------------------------------
    // processes
    // -------------------------------------------------------------------------

    /**
     * Runs a tool which must succeed.
     */
    private void runChecked(ExternalSystemTaskId id,
                            ExternalSystemTaskNotificationListener listener,
                            GeneralCommandLine commandLine,
                            @Nullable StringBuilder stdout,
                            boolean echoStdout) throws ExternalSystemException {
        int exitCode = run(id, listener, commandLine, stdout, echoStdout);
        if (exitCode != 0) {
            throw new ExternalSystemException(MakeBuildLocalize.errorExitCode(commandLine.getExePath(), exitCode).get());
        }
    }

    /**
     * Runs a tool: its standard error goes to the console, and its standard output into {@code stdout} - and to the
     * console too, when {@code echoStdout}.
     */
    private int run(ExternalSystemTaskId id,
                    ExternalSystemTaskNotificationListener listener,
                    GeneralCommandLine commandLine,
                    @Nullable StringBuilder stdout,
                    boolean echoStdout) throws ExternalSystemException {
        listener.onTaskOutput(id, "Running: " + commandLine.getCommandLineString() + "\n", true);
        int exitCode;
        try {
            exitCode = myRunner.run(commandLine, (text, isStdout) -> {
                if (isStdout && stdout != null) {
                    stdout.append(text);
                }
                if (!isStdout || echoStdout) {
                    listener.onTaskOutput(id, text, isStdout);
                }
            });
        }
        catch (ExecutionException e) {
            throw new ExternalSystemException(MakeBuildLocalize.errorFailedRun(commandLine.getExePath(), String.valueOf(e.getMessage())).get());
        }
        if (exitCode == MakeProcessRunner.CANCELLED_EXIT_CODE) {
            throw new ExternalSystemException(MakeBuildLocalize.errorCancelled().get());
        }
        return exitCode;
    }

    private static void notify(ExternalSystemTaskNotificationListener listener, ExternalSystemTaskId id, String message) {
        listener.onStatusChange(new ExternalSystemTaskNotificationEvent(id, message));
    }

    @Override
    public boolean cancelTask(ExternalSystemTaskId taskId, ExternalSystemTaskNotificationListener listener) {
        myRunner.cancel();
        return true;
    }
}
