package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.application.util.function.CommonProcessors;
import consulo.project.content.scope.ProjectAwareSearchScope;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.StringStubIndexExtension;
import consulo.language.psi.stub.StubIndex;
import consulo.language.psi.stub.StubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;
import consulo.project.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ExtensionImpl
public class MakefileTargetIndex extends StringStubIndexExtension<MakefileTarget> {
    public static final StubIndexKey<String, MakefileTarget> TARGET_INDEX_KEY = StubIndexKey.createIndexKey("makefile.target.index");

    public List<MakefileTarget> allTargets(Project project) {
        Set<String> allTargets = new LinkedHashSet<>();
        processAllKeys(project, new CommonProcessors.CollectProcessor<>(allTargets));
        List<MakefileTarget> result = new ArrayList<>();
        for (String it : allTargets) {
            result.addAll(getTargets(it, project, GlobalSearchScope.projectScope(project)));
        }
        return result;
    }

    @Override
    public StubIndexKey<String, MakefileTarget> getKey() {
        return TARGET_INDEX_KEY;
    }

    @Override
    public Collection<MakefileTarget> get(String key, Project project, ProjectAwareSearchScope scope) {
        return getTargets(key, project, scope);
    }

    public Collection<MakefileTarget> getTargets(String key, Project project, ProjectAwareSearchScope scope) {
        return StubIndex.getElements(TARGET_INDEX_KEY, key, project, scope, MakefileTarget.class);
    }

    public static MakefileTargetIndex getInstance() {
        return Application.get().getExtensionPoint(StubIndexExtension.class).findExtensionOrFail(MakefileTargetIndex.class);
    }
}
