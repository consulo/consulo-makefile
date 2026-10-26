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
package consulo.makefile.build.run;

import com.jetbrains.lang.makefile.MakefileLanguage;
import com.jetbrains.lang.makefile.MakefileUtil;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileTargets;
import com.jetbrains.lang.makefile.psi.MakefileTypes;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.lineMarker.RunLineMarkerContributor;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.ui.ex.action.AnAction;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Port of the JetBrains {@code MakefileTargetRunLineMarkerContributor}: the gutter icon runs the targets of a rule -
 * through the Make external system, as {@link MakefileRunTargetAction} does.
 */
@ExtensionImpl
public class MakefileTargetRunLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    @RequiredReadAction
    public @Nullable Info getInfo(PsiElement element) {
        if (element.getNode().getElementType() == MakefileTypes.CHARS) {
            PsiElement target = element.getParent();
            if (target == null || target.getFirstChild() != element) {
                return null;
            }
            if (target instanceof MakefileTarget) {
                if (!(target.getParent() instanceof MakefileTargets targets)) {
                    return null;
                }
                List<MakefileTarget> targetList = targets.getTargetList();
                if (!targetList.isEmpty() && targetList.get(0) == target
                    && targetList.stream().anyMatch(it -> !it.isSpecialTarget())) {
                    return new Info(
                        MakefileUtil.MAKEFILE_TARGET_ICON,
                        it -> "",
                        targetList.stream().map(MakefileRunTargetAction::new).toArray(AnAction[]::new)
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
