package com.jetbrains.lang.makefile.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;

/**
 * A {@code Makefile} <i>recipe</i> may be a host for injected shell script (each <i>recipe</i>
 * line, after a leading tab and optional {@code @} or {@code -} characters, is an injected
 * shell script fragment).
 */
@ExtensionImpl
public class MakefileRecipeManipulator extends AbstractElementManipulator<MakefileRecipe> {
    @Override
    public MakefileRecipe handleContentChange(MakefileRecipe element, TextRange range, String newContent) {
        return (MakefileRecipe) element.updateText(newContent);
    }

    @Override
    public Class<MakefileRecipe> getElementClass() {
        return MakefileRecipe.class;
    }
}
