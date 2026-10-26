package com.jetbrains.lang.makefile.psi.impl;

import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;

public interface IMakeFileCommand extends PsiElement {
    TextRange getShellCommandRange();
}
