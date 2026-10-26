package com.jetbrains.lang.makefile.inspections;

import com.jetbrains.lang.makefile.MakefileLanguage;
import com.jetbrains.lang.makefile.MakefileTargetReference;
import com.jetbrains.lang.makefile.MakefileUtil;
import com.jetbrains.lang.makefile.psi.MakefilePrerequisite;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileTargetLine;
import com.jetbrains.lang.makefile.psi.MakefileVisitor;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.LocalInspectionToolSession;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.language.psi.path.FileReference;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

@ExtensionImpl
public class MakefileUnresolvedPrerequisiteInspection extends LocalInspectionTool {
    private static final Pattern VARIABLE_USAGE = Pattern.compile("\\$\\((.*)\\)");

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly, LocalInspectionToolSession session, Object state) {
        return new MakefileVisitor() {
            @Override
            public void visitPrerequisite(MakefilePrerequisite prerequisite) {
                if (VARIABLE_USAGE.matcher(prerequisite.getText()).matches()) {
                    return;
                }

                MakefileTargetLine targetLine = MakefileUtil.findTargetLine(prerequisite);
                if (targetLine == null) {
                    return;
                }
                List<MakefileTarget> targets = targetLine.getTargets().getTargetList();

                if (targets.stream().anyMatch(MakefileTarget::isPatternTarget) && prerequisite.textContains('%')) {
                    // don't highlight such cases cause it leads to a lot of false errors
                    return;
                }

                if (!targets.isEmpty() && !targets.get(0).isSpecialTarget() && targetLine.getTargetPattern() == null) {
                    boolean targetReferences = false;
                    for (PsiReference it : prerequisite.getReferences()) {
                        if (it instanceof MakefileTargetReference targetReference && targetReference.multiResolve(false).length > 0) {
                            targetReferences = true;
                            break;
                        }
                    }

                    boolean fileReferenceResolved = false;
                    TextRange unresolvedFile = null;
                    for (PsiReference it : prerequisite.getReferences()) {
                        if (!(it instanceof FileReference)) {
                            continue;
                        }
                        if (it.resolve() == null) {
                            if (!targetReferences) {
                                unresolvedFile = unresolvedFile != null ? unresolvedFile.union(it.getRangeInElement()) : it.getRangeInElement();
                            }
                        }
                        else {
                            fileReferenceResolved = true;
                        }
                    }

                    if (!targetReferences && !fileReferenceResolved) {
                        CreateRuleFix fix = new CreateRuleFix();
                        holder.newProblem(MakefileLocalize.inspectionMessageUnresolvedPrerequisite())
                            .range(prerequisite)
                            .withFixes(fix)
                            .create();
                    }
                    else if (unresolvedFile != null) {
                        holder.newProblem(MakefileLocalize.inspectionMessageFileNotFound())
                            .range(prerequisite, unresolvedFile)
                            .create();
                    }
                }

            }
        };
    }

    @Override
    public @Nullable Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }

    @Override
    public String getShortName() {
        return "MakefileUnresolvedPrerequisite";
    }

    @Override
    public LocalizeValue getGroupDisplayName() {
        return MakefileLocalize.makefileInspectionGroupName();
    }

    @Override
    public LocalizeValue getDisplayName() {
        return MakefileLocalize.makeUnresolvedPrerequisiteInspectionName();
    }

    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WEAK_WARNING;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
