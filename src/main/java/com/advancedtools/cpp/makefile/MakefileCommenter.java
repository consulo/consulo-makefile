package com.advancedtools.cpp.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Commenter;
import consulo.language.Language;
import org.jetbrains.annotations.Nullable;

import jakarta.annotation.Nonnull;

/**
 * @author maxim
 *         Date: 2/3/12
 *         Time: 1:30 PM
 */
@ExtensionImpl
public class MakefileCommenter implements Commenter
{
	@Nullable
	public String getLineCommentPrefix()
	{
		return "#";
	}

	@Nullable
	public String getBlockCommentPrefix()
	{
		return null;
	}

	@Nullable
	public String getBlockCommentSuffix()
	{
		return null;
	}

	public String getCommentedBlockCommentPrefix()
	{
		return null;
	}

	public String getCommentedBlockCommentSuffix()
	{
		return null;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return MakefileLanguage.INSTANCE;
	}
}
