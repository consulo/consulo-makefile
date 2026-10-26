package com.jetbrains.lang.makefile.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;

/**
 * A {@code Makefile} <i>function</i> may be a host for injected shell script, e. g.:
 *
 * <pre>
 * $(shell uname -a)
 * </pre>
 * <p>
 * Here, {@code $(shell)} is a <i>function</i>, and {@code uname -a} is an injected shell
 * script fragment.
 */
@ExtensionImpl
public class MakefileFunctionManipulator extends AbstractElementManipulator<MakefileFunction> {
    @Override
    public MakefileFunction handleContentChange(MakefileFunction element, TextRange range, String newContent) {
        return (MakefileFunction) element.updateText(newContent);
    }

    @Override
    public Class<MakefileFunction> getElementClass() {
        return MakefileFunction.class;
    }
}
