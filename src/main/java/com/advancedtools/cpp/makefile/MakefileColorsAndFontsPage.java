package com.advancedtools.cpp.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.TextAttributesKey;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.colorScheme.setting.ColorDescriptor;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.makefile.MakefileBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maxim
 *         Date: 1/29/13
 *         Time: 11:22 AM
 */
@ExtensionImpl
public class MakefileColorsAndFontsPage implements ColorSettingsPage
{
	private static final AttributesDescriptor[] ATTRS;

	static
	{
		ATTRS = new AttributesDescriptor[]{
				new AttributesDescriptor(MakefileBundle.message("make.keyword"), MakefileSyntaxHighlighter.MAKEFILE_KEYWORD),
				new AttributesDescriptor(MakefileBundle.message("make.linecomment"), MakefileSyntaxHighlighter.MAKEFILE_LINE_COMMENT),
				new AttributesDescriptor(MakefileBundle.message("make.templatedata"), MakefileSyntaxHighlighter.MAKEFILE_TEMPLATE_DATA),
				new AttributesDescriptor(MakefileBundle.message("make.target"), MakefileSyntaxHighlighter.MAKEFILE_TARGET),
				new AttributesDescriptor(MakefileBundle.message("make.definition"), MakefileSyntaxHighlighter.MAKEFILE_DEFINITION),
		};
	}

	private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];
	private static Map<String, TextAttributesKey> ourTags = new HashMap<String, TextAttributesKey>();

	@NotNull
	public String getDisplayName()
	{
		return "Makefile";
	}

	@NotNull
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return ATTRS;
	}

	@NotNull
	public ColorDescriptor[] getColorDescriptors()
	{
		return COLORS;
	}

	@NotNull
	public SyntaxHighlighter getHighlighter()
	{
		return new MakefileSyntaxHighlighter();
	}

	@NonNls
	@NotNull
	public String getDemoText()
	{
		return "EXE = $(OUTPUT_PATH)${Executable}\n" +
				"ifeq ($(OS),Windows_NT)\n" +
				"  OBJ = obj\n" +
				"endif\n" +
				"# INCLUDES = -I../.includes\n" +
				"rebuild: clean";
	}

	@Nullable
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return ourTags;
	}
}
