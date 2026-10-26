package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Commenter;
import consulo.language.Language;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class MakefileCommenter implements Commenter {
    @Override
    public String getLineCommentPrefix() {
        return "#";
    }

    @Override
    public String getCommentedBlockCommentPrefix() {
        return "";
    }

    @Override
    public @Nullable String getCommentedBlockCommentSuffix() {
        return null;
    }

    @Override
    public @Nullable String getBlockCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getBlockCommentSuffix() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
