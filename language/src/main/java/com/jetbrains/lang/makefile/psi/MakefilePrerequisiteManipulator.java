package com.jetbrains.lang.makefile.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;

@ExtensionImpl
public class MakefilePrerequisiteManipulator extends AbstractElementManipulator<MakefilePrerequisite> {
    @Override
    public MakefilePrerequisite handleContentChange(MakefilePrerequisite element, TextRange textRange, String newContent) {
        return element.updateText(newContent);
    }

    @Override
    public Class<MakefilePrerequisite> getElementClass() {
        return MakefilePrerequisite.class;
    }
}
