package com.advancedtools.cpp.makefile.lang;

import com.advancedtools.cpp.makefile.MakefileTokenTypes;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;
import org.jetbrains.annotations.NotNull;

/**
* @author VISTALL
* @since 18:46/16.03.13
*/
public class MakefileParser implements PsiParser
{
	@NotNull
	public ASTNode parse(IElementType iElementType, PsiBuilder psiBuilder, LanguageVersion languageVersion)
	{
		final PsiBuilder.Marker marker = psiBuilder.mark();
		PsiBuilder.Marker statement = psiBuilder.mark();
		IElementType statementType = null;

		while(!psiBuilder.eof())
		{
			final IElementType tokenType = psiBuilder.getTokenType();

			if(tokenType == MakefileTokenTypes.SEMANTIC_WHITESPACE)
			{
				psiBuilder.advanceLexer();
				statement.done(statementType != null ? statementType : MakefileTokenTypes.STATEMENT);
				statementType = null;
				statement = psiBuilder.mark();
				continue;
			}
			else if(shouldProduceComposite(tokenType))
			{
				PsiBuilder.Marker identifier = psiBuilder.mark();
				psiBuilder.advanceLexer();
				identifier.done(tokenType);
				continue;
			}
			else if(tokenType == MakefileTokenTypes.VAR_DEFINITION)
			{
				statementType = tokenType;
			}
			else if(tokenType == MakefileTokenTypes.IDENTIFIER_START)
			{
				readIdentifier(psiBuilder, MakefileTokenTypes.IDENTIFIER_END, MakefileTokenTypes.IDENTIFIER);
			}
			else if(tokenType == MakefileTokenTypes.TARGET_IDENTIFIER_START)
			{
				readIdentifier(psiBuilder, MakefileTokenTypes.TARGET_IDENTIFIER_END, MakefileTokenTypes.TARGET_IDENTIFIER);
				if(!psiBuilder.eof())
					psiBuilder.advanceLexer();

				statementType = MakefileTokenTypes.TARGET_DECLARATION;
				continue;
			}
			psiBuilder.advanceLexer();
		}

		statement.done(MakefileTokenTypes.STATEMENT);
		marker.done(iElementType);
		return psiBuilder.getTreeBuilt();
	}

	private static void readIdentifier(PsiBuilder psiBuilder, IElementType identifierEnd, IElementType identifierType)
	{
		PsiBuilder.Marker identifier = psiBuilder.mark();
		psiBuilder.advanceLexer();
		IElementType currentType;

		while(!psiBuilder.eof() && (currentType = psiBuilder.getTokenType()) != identifierEnd)
		{
			PsiBuilder.Marker marker = shouldProduceComposite(currentType) ? psiBuilder.mark() : null;
			psiBuilder.advanceLexer();
			if(marker != null)
				marker.done(currentType);
		}
		identifier.done(identifierType);
	}

	public static boolean shouldProduceComposite(IElementType tokenType)
	{
		return tokenType == MakefileTokenTypes.IDENTIFIER || tokenType == MakefileTokenTypes.VAR_REFERENCE;
	}

	public static boolean shouldProduceDefinition(IElementType elementType)
	{
		return elementType == MakefileTokenTypes.TARGET_DECLARATION || elementType == MakefileTokenTypes.VAR_DEFINITION;
	}
}
