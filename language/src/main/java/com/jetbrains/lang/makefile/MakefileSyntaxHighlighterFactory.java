package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighterFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class MakefileSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new MakefileSyntaxHighlighter();
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
