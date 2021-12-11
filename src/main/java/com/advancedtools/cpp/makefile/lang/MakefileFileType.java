package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
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

	@NotNull
	@NonNls
	public String getId()
	{
		return "Makefile";
	}

	@NotNull
	public LocalizeValue getDescription()
	{
		return LocalizeValue.localizeTODO("Traditional makefiles");
	}

	@NotNull
	@NonNls
	public String getDefaultExtension()
	{
		return "";
	}

	@Nullable
	public Image getIcon()
	{
		return PlatformIconGroup.fileTypesText();
	}
}
