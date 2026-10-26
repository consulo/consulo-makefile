package com.jetbrains.lang.makefile.psi;

import com.jetbrains.lang.makefile.MakefileUtil;
import consulo.language.psi.PsiFile;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;

public class MakefileTargetPresentation implements ItemPresentation {
    private final MakefileTarget myTarget;

    public MakefileTargetPresentation(MakefileTarget target) {
        myTarget = target;
    }

    @Override
    public Image getIcon() {
        return MakefileUtil.MAKEFILE_TARGET_ICON;
    }

    @Override
    public LocalizeValue getPresentableText() {
        return LocalizeValue.of(myTarget.getText());
    }

    @Override
    public String getLocationString() {
        PsiFile file = myTarget.getContainingFile();
        VirtualFile virtualFile = file != null ? file.getVirtualFile() : null;
        return "in " + (virtualFile != null ? virtualFile.getPresentableName() : null);
    }
}
