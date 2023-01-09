package com.advancedtools.cpp.makefile.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 19:01/16.03.13
 */
@ExtensionImpl
public class MakefileFileTypeFactory extends FileTypeFactory
{
	private final FileNameMatcherFactory myFileNameMatcherFactory;

	@Inject
	public MakefileFileTypeFactory(FileNameMatcherFactory fileNameMatcherFactory)
	{
		myFileNameMatcherFactory = fileNameMatcherFactory;
	}

	@Override
	public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
	{
		List<FileNameMatcher> matcherList = new ArrayList<FileNameMatcher>();
		matcherList.add(myFileNameMatcherFactory.createExactFileNameMatcher("Makefile"));

		fileTypeConsumer.consume(MakefileFileType.INSTANCE, matcherList.toArray(new FileNameMatcher[matcherList.size()]));
	}
}
