package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.documentation.LanguageDocumentationProvider;
import consulo.language.psi.PsiElement;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class MakefileDocumentationProvider implements LanguageDocumentationProvider {
    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return element instanceof MakefileTarget target ? target.getDocComment() : null;
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return element instanceof MakefileTarget target ? target.getDocComment() : null;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
