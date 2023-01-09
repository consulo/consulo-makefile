/* AdvancedTools, 2007, all rights reserved */
package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NonNls;

/**
 * @author maxim
 *         Date: 21.09.2006
 *         Time: 5:29:48
 */
public class MakefileElementType extends IElementType
{
	public MakefileElementType(@NonNls String s)
	{
		super(s, MakefileLanguage.INSTANCE);
	}
}