package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.function.Processor;
import consulo.project.content.scope.ProjectAwareSearchScope;
import consulo.content.scope.SearchScope;
import consulo.ide.navigation.GotoSymbolContributor;
import consulo.language.psi.search.FindSymbolParameters;
import consulo.language.psi.stub.IdFilter;
import consulo.navigation.NavigationItem;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class MakefileChooseByNameContributor implements GotoSymbolContributor {
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        return MakefileUtil.findTargets(project, name).stream().filter(it -> !it.isSpecialTarget()).toArray(NavigationItem[]::new);
    }

    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return MakefileUtil.findAllTargets(project).stream().filter(it -> !StringUtil.isEmpty(it)).toArray(String[]::new);
    }

    @Override
    public void processNames(Processor<String> processor, SearchScope scope, @Nullable IdFilter filter) {
        Project project = scope instanceof ProjectAwareSearchScope projectScope ? projectScope.getProject() : null;
        if (project == null) {
            return;
        }
        for (String name : getNames(project, false)) {
            if (!processor.process(name)) {
                return;
            }
        }
    }

    @Override
    public void processElementsWithName(String name, Processor<NavigationItem> processor, FindSymbolParameters parameters) {
        for (NavigationItem item : getItemsByName(name, name, parameters.getProject(), false)) {
            if (!processor.process(item)) {
                return;
            }
        }
    }
}
