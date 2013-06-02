package com.advancedtools.cpp.makefile.lang;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.napile.cpp4idea.makefile.icons.MakefileIcons;
import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;

/**
 * @author VISTALL
 * @since 18:34/16.03.13
 */
public class MakefileFileType extends LanguageFileType
{
	public static LanguageFileType INSTANCE = new MakefileFileType();

	public MakefileFileType()
	{
		super(MakefileLanguage.INSTANCE);
	}

	@NotNull
	@NonNls
	public String getName()
	{
		return "Makefile";
	}

	@NotNull
	public String getDescription()
	{
		return "Traditional makefiles";
	}

	@NotNull
	@NonNls
	public String getDefaultExtension()
	{
		return "";
	}

	@Nullable
	public Icon getIcon()
	{
		return MakefileIcons.MAKEFILE_FILE;
	}
}
