package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileLanguage;
import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import com.advancedtools.cpp.makefile.psi.MakefileNamedElement;
import com.advancedtools.cpp.makefile.psi.MakefilePsiElement;
import com.advancedtools.cpp.makefile.psi.MakefilePsiFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;
import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nonnull;

/**
 * @author maxim
 *         Date: 2/3/12
 *         Time: 1:28 PM
 */
@ExtensionImpl
public class MakefileParserDefinition implements ParserDefinition
{
	@Nonnull
	@Override
	public Language getLanguage()
	{
		return MakefileLanguage.INSTANCE;
	}

	@NotNull
	public Lexer createLexer(LanguageVersion languageVersion)
	{
		return new MakefileParsingLexer();
	}

	public PsiParser createParser(LanguageVersion languageVersion)
	{
		return new MakefileParser();
	}

	public IFileElementType getFileNodeType()
	{
		return MakefileTokenTypes.MAKE_FILE;
	}

	@NotNull
	public TokenSet getWhitespaceTokens(LanguageVersion languageVersion)
	{
		return MakefileTokenTypes.WHITE_SPACES;
	}

	@NotNull
	public TokenSet getCommentTokens(LanguageVersion languageVersion)
	{
		return MakefileTokenTypes.COMMENTS;
	}

	// IDEA8
	@NotNull
	public TokenSet getStringLiteralElements(LanguageVersion languageVersion)
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
