package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileNamedElement;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileVariable;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.TokenSet;
import consulo.language.cacheBuilder.DefaultWordsScanner;
import consulo.language.cacheBuilder.WordsScanner;
import consulo.language.findUsage.FindUsagesProvider;
import consulo.language.psi.PsiElement;
import consulo.makefile.localize.MakefileLocalize;
import org.jspecify.annotations.Nullable;

import static com.jetbrains.lang.makefile.psi.MakefileTypes.CHARS;
import static com.jetbrains.lang.makefile.psi.MakefileTypes.COMMENT;

@ExtensionImpl
public class MakefileFindUsagesProvider implements FindUsagesProvider {
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new MakefileLexerAdapter(), TokenSet.create(CHARS), TokenSet.create(COMMENT), TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(PsiElement element) {
        if (element instanceof MakefileTarget target) {
            return !target.isSpecialTarget();
        }
        return element instanceof MakefileNamedElement;
    }

    @Override
    public String getType(PsiElement element) {
        if (element instanceof MakefileTarget target) {
            return !target.isSpecialTarget() ? MakefileLocalize.usageTypeMakefileTarget().get() : "";
        }
        if (element instanceof MakefileVariable) {
            return MakefileLocalize.usageTypeMakefileVariable().get();
        }
        return "";
    }

    @Override
    public String getDescriptiveName(PsiElement element) {
        return canFindUsagesFor(element) ? element.getText() : "";
    }

    @Override
    public String getNodeText(PsiElement element, boolean useFullName) {
        return canFindUsagesFor(element) ? element.getText() : "";
    }

    @Override
    public @Nullable String getHelpId(PsiElement element) {
        return null;
    }

    @Override
    public Language getLanguage() {
        return MakefileLanguage.INSTANCE;
    }
}
