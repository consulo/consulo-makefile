package com.advancedtools.cpp.makefile.psi;

import org.jetbrains.annotations.NotNull;
import com.advancedtools.cpp.makefile.lang.MakefileFileType;
import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;

/**
 * @author maxim
 *         Date: 2/7/12
 *         Time: 12:53 PM
 */
public class MakefilePsiFile extends PsiFileBase
{
	public MakefilePsiFile(@NotNull FileViewProvider fileViewProvider)
	{
		super(fileViewProvider, MakefileLanguage.INSTANCE);
	}

	@NotNull
	public FileType getFileType()
	{
		return MakefileFileType.INSTANCE;
	}

	@Override
	public String toString()
	{
		return "MakeFile:" + getName();
	}
}
