package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefilePrerequisite;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileTargetLine;
import consulo.execution.icon.ExecutionIconGroup;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;
import consulo.ui.image.Image;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * Port of the JetBrains {@code utils.kt} top-level declarations.
 */
public final class MakefileUtil {
    public static final Image MAKEFILE_TARGET_ICON = ExecutionIconGroup.gutterRun();

    private MakefileUtil() {
    }

    public static Collection<String> findAllTargets(Project project) {
        return MakefileTargetIndex.getInstance().getAllKeys(project);
    }

    public static Collection<MakefileTarget> findTargets(Project project, String name) {
        return MakefileTargetIndex.getInstance().getTargets(name, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<MakefileTarget> findTargets(PsiFile psiFile) {
        return PsiTreeUtil.findChildrenOfType(psiFile, MakefileTarget.class);
    }

    public static @Nullable MakefileTargetLine findTargetLine(MakefilePrerequisite prerequisite) {
        return prerequisite.getParent().getParent().getParent() instanceof MakefileTargetLine targetLine ? targetLine : null;
    }
}
