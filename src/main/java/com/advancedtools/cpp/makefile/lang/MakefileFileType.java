package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.makefile.icon.MakeFileIconGroup;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	@Override
    @NotNull
	@NonNls
	public String getId()
	{
		return "Makefile";
	}

	@Override
    @NotNull
	public LocalizeValue getDescription()
	{
		return LocalizeValue.localizeTODO("Traditional makefiles");
	}

	@Override
    @NotNull
	@NonNls
	public String getDefaultExtension()
	{
		return "";
	}

	@Override
    @Nullable
	public Image getIcon()
	{
		return MakeFileIconGroup.makefile();
	}
}
