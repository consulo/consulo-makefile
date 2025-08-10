package com.advancedtools.cpp.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.colorScheme.setting.ColorDescriptor;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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
				new AttributesDescriptor(MakefileLocalize.makeKeyword(), MakefileSyntaxHighlighter.MAKEFILE_KEYWORD),
				new AttributesDescriptor(MakefileLocalize.makeLinecomment(), MakefileSyntaxHighlighter.MAKEFILE_LINE_COMMENT),
				new AttributesDescriptor(MakefileLocalize.makeTemplatedata(), MakefileSyntaxHighlighter.MAKEFILE_TEMPLATE_DATA),
				new AttributesDescriptor(MakefileLocalize.makeTarget(), MakefileSyntaxHighlighter.MAKEFILE_TARGET),
				new AttributesDescriptor(MakefileLocalize.makeDefinition(), MakefileSyntaxHighlighter.MAKEFILE_DEFINITION),
		};
	}

	private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

	@NotNull
	public LocalizeValue getDisplayName()
	{
		return MakefileLocalize.makefileText();
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
}
