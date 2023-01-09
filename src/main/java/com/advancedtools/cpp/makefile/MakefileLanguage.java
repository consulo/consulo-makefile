/* AdvancedTools, 2007, all rights reserved */
package com.advancedtools.cpp.makefile;

import consulo.language.Language;

/**
 * @author maxim
 */
public class MakefileLanguage extends Language
{
	public static final Language INSTANCE = new MakefileLanguage();

	public MakefileLanguage()
	{
		super("Makefile");
	}
}
