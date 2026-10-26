package com.jetbrains.lang.makefile.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;

/**
 * A {@code Makefile} <i>substitution</i> may be a host for injected shell script, e. g.:
 *
 * <pre>
 * `uname -a`
 * </pre>
 * <p>
 * Here, the backticks denote a <i>substitution</i> (a sub-shell invocation), and
 * {@code uname -a} is an injected shell script fragment.
 */
@ExtensionImpl
public class MakefileSubstitutionManipulator extends AbstractElementManipulator<MakefileSubstitution> {
    @Override
    public MakefileSubstitution handleContentChange(MakefileSubstitution element, TextRange range, String newContent) {
        return (MakefileSubstitution) element.updateText(newContent);
    }

    @Override
    public Class<MakefileSubstitution> getElementClass() {
        return MakefileSubstitution.class;
    }
}
