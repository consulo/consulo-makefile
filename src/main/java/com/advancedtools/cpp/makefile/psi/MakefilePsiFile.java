package com.advancedtools.cpp.makefile.psi;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.advancedtools.cpp.makefile.lang.MakefileFileType;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.NotNull;
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
