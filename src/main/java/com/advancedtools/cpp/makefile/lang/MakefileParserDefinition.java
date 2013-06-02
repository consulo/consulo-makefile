package com.advancedtools.cpp.makefile.lang;

import org.jetbrains.annotations.NotNull;
import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import com.advancedtools.cpp.makefile.psi.MakefileNamedElement;
import com.advancedtools.cpp.makefile.psi.MakefilePsiElement;
import com.advancedtools.cpp.makefile.psi.MakefilePsiFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author maxim
 *         Date: 2/3/12
 *         Time: 1:28 PM
 */
public class MakefileParserDefinition implements ParserDefinition
{
	@NotNull
	public Lexer createLexer(Project project, Module module)
	{
		return new MakefileParsingLexer();
	}

	public PsiParser createParser(Project project)
	{
		return new MakefileParser();
	}

	public IFileElementType getFileNodeType()
	{
		return MakefileTokenTypes.MAKE_FILE;
	}

	@NotNull
	public TokenSet getWhitespaceTokens()
	{
		return MakefileTokenTypes.WHITE_SPACES;
	}

	@NotNull
	public TokenSet getCommentTokens()
	{
		return MakefileTokenTypes.COMMENTS;
	}

	// IDEA8
	@NotNull
	public TokenSet getStringLiteralElements()
	{
		return MakefileTokenTypes.LITERALS;
	}

	@NotNull
	public PsiElement createElement(ASTNode astNode)
	{
		if(MakefileParser.shouldProduceDefinition(astNode.getElementType()))
		{
			return new MakefileNamedElement(astNode);
		}
		return new MakefilePsiElement(astNode);
	}

	public PsiFile createFile(FileViewProvider fileViewProvider)
	{
		return new MakefilePsiFile(fileViewProvider);
	}

	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1)
	{
		return SpaceRequirements.MAY;
	}

	static class MakefileParsingLexer extends MakefileLexer
	{
		private CharSequence sequence;
		private final int ON_COMMENT_OR_CONTINUE_STATEMENT = _MakefileLexer.CONTINUE + 1;
		private final int ON_SEMANTIC_LF = ON_COMMENT_OR_CONTINUE_STATEMENT + 1;

		boolean hasPreviousCommentOrContinue;
		boolean onSemanticLineFeed;

		public MakefileParsingLexer()
		{
			super(false);
		}

		public void advance()
		{
			if(!onSemanticLineFeed)
			{
				super.advance();

				IElementType tokenType = getTokenType();
				if(!hasPreviousCommentOrContinue && tokenType == MakefileTokenTypes.WHITE_SPACE)
				{
					if(sequence == null)
						sequence = getBufferSequence();
					final int tokenEnd = getTokenEnd();

					for(int i = getTokenStart(); i < tokenEnd; ++i)
					{
						if(sequence.charAt(i) == '\n')
						{
							onSemanticLineFeed = true;
							break;
						}
					}
				}

				hasPreviousCommentOrContinue = tokenType == MakefileTokenTypes.END_OF_LINE_COMMENT || tokenType == MakefileTokenTypes.CONTINUE_STATEMENT;
			}
			else
			{
				onSemanticLineFeed = false;
				hasPreviousCommentOrContinue = false;
			}
		}

		public IElementType getTokenType()
		{
			return onSemanticLineFeed ? MakefileTokenTypes.SEMANTIC_WHITESPACE : super.getTokenType();
		}

		public int getTokenEnd()
		{
			return onSemanticLineFeed ? getTokenStart() : super.getTokenEnd();
		}

		public int getState()
		{
			if(onSemanticLineFeed)
				return ON_SEMANTIC_LF;
			if(hasPreviousCommentOrContinue)
				return ON_COMMENT_OR_CONTINUE_STATEMENT;
			return super.getState();
		}
	}

}
