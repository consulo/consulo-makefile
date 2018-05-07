package com.advancedtools.cpp.makefile;

import java.util.Map;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.HashMap;
import consulo.makefile.MakefileBundle;

/**
 * @author maxim
 *         Date: 1/29/13
 *         Time: 11:22 AM
 */
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
	private static
	@NonNls
	Map<String, TextAttributesKey> ourTags = new HashMap<String, TextAttributesKey>();

	static
	{
	}

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
