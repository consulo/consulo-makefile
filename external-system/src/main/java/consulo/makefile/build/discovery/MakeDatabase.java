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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The targets a makefile offers to build, read from the data base {@code make --print-data-base --question} prints -
 * so targets of included and generated makefiles are there too, as make itself sees them.
 *
 * @param defaultGoal what a plain {@code make} builds, {@code null} when the makefile has no rule
 * @param targets     the targets worth building by name, the default goal first
 * @param allTargets  every rule of the makefiles
 * @author VISTALL
 * @since 2026-10-25
 */
public record MakeDatabase(@Nullable String defaultGoal, List<String> targets, Set<String> allTargets) {
    public static final MakeDatabase EMPTY = new MakeDatabase(null, List.of(), Set.of());

    private static final int MAX_TARGETS = 200;

    private static final Pattern DEFAULT_GOAL = Pattern.compile("\\.DEFAULT_GOAL :?= (\\S+)");
    private static final Pattern RULE = Pattern.compile("([^#\\s:=%][^:=]*?)::?(?:\\s(.*))?");

    /**
     * The internals of an automake makefile: its recursion, the steps of its install and dist targets, the documentation
     * formats.
     */
    private static final Pattern AUTOMAKE_INTERNAL = Pattern.compile(
        "am--.*|.*-(?:am|recursive|generic|local|hook|hdr)|.*-(?:bzip2|bzip3|gzip|lzip|shar|tarZ|xz|zip|zstd)"
            + "|install-(?:data|dvi|exec|html|info|man|pdf|ps)|dvi|html|info|pdf|ps|installdirs|cscope|cscopelist|ctags|CTAGS"
            + "|GTAGS|TAGS|ID|tags|distdir|distuninstallcheck|distcleancheck|mostlyclean|maintainer-clean|install-strip|uninstall"
            + "|clean-cscope|dist-all|distclean-tags|stamp-h\\d*|configure|aclocal|autoheader"
    );

    public static MakeDatabase parse(String output) {
        String defaultGoal = null;
        Set<String> rules = new LinkedHashSet<>();
        Set<String> phony = new LinkedHashSet<>();
        boolean inFiles = false;
        boolean notTarget = false;

        for (String line : output.split("\\r?\\n")) {
            Matcher goal = DEFAULT_GOAL.matcher(line);
            if (goal.matches()) {
                defaultGoal = goal.group(1);
                continue;
            }
            if (line.equals("# Files")) {
                inFiles = true;
                continue;
            }
            if (line.startsWith("# Finished Make data base") || line.equals("# VPATH Search Paths")) {
                inFiles = false;
                continue;
            }
            if (!inFiles) {
                continue;
            }
            if (line.equals("# Not a target:")) {
                notTarget = true;
                continue;
            }
            Matcher rule = RULE.matcher(line);
            if (!rule.matches()) {
                continue;
            }
            String name = rule.group(1).trim();
            if (name.equals(".PHONY")) {
                String prerequisites = rule.group(2);
                if (prerequisites != null) {
                    for (String prerequisite : prerequisites.trim().split("\\s+")) {
                        if (!prerequisite.isEmpty()) {
                            phony.add(prerequisite);
                        }
                    }
                }
            }
            else if (!notTarget) {
                rules.add(name);
            }
            notTarget = false;
        }

        List<String> offered = new ArrayList<>();
        for (String name : rules) {
            if (!name.equals(defaultGoal) && isOffered(name, phony.contains(name))) {
                offered.add(name);
            }
        }
        // too many to list - OpenJDK has thousands, clean-java.base-native and the like: the simplest names are the
        // entry points (images, hotspot, test)
        if (offered.size() > MAX_TARGETS) {
            offered.sort(Comparator.comparingInt(MakeDatabase::separatorCount).thenComparingInt(String::length));
            offered = offered.subList(0, MAX_TARGETS);
        }

        List<String> targets = new ArrayList<>();
        if (defaultGoal != null) {
            targets.add(defaultGoal);
        }
        targets.addAll(offered);
        return new MakeDatabase(defaultGoal, targets, Set.copyOf(rules));
    }

    public boolean hasTarget(String name) {
        return allTargets.contains(name);
    }

    private static int separatorCount(String name) {
        int count = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '-' || c == '.' || c == '_') {
                count++;
            }
        }
        return count;
    }

    /**
     * A phony target, or a program - a file target without an extension; not the objects, the makefiles and the
     * internals of automake.
     */
    private static boolean isOffered(String name, boolean phony) {
        if (name.startsWith(".") || name.contains("/") || name.contains("$") || name.contains(" ")) {
            return false;
        }
        if (AUTOMAKE_INTERNAL.matcher(name).matches()) {
            return false;
        }
        if (phony) {
            return true;
        }
        return !name.contains(".") && !name.equals("Makefile") && !name.equals("makefile") && !name.equals("GNUmakefile");
    }
}
