package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;

public class MakefileTargetStructureViewPresentation implements ItemPresentation {
    private final MakefileTarget myTarget;

    public MakefileTargetStructureViewPresentation(MakefileTarget target) {
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
        return "";
    }
}
