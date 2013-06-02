package com.advancedtools.cpp.makefile.lang;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * @author VISTALL
 * @since 19:01/16.03.13
 */
public class MakefileFileTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
	{
		List<FileNameMatcher> matcherList = new ArrayList<FileNameMatcher>();
		matcherList.add(new ExactFileNameMatcher("Makefile"));
		if(ApplicationManager.getApplication().isUnitTestMode())
			matcherList.add(new ExtensionFileNameMatcher("mk"));

		fileTypeConsumer.consume(MakefileFileType.INSTANCE, matcherList.toArray(new FileNameMatcher[matcherList.size()]));
	}
}
