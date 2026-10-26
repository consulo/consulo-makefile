package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileCommand;
import com.jetbrains.lang.makefile.psi.MakefileFunction;
import com.jetbrains.lang.makefile.psi.MakefileFunctionParam;
import com.jetbrains.lang.makefile.psi.MakefileRecipe;
import com.jetbrains.lang.makefile.psi.MakefileSubstitution;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.inject.MultiHostInjector;
import consulo.language.inject.MultiHostRegistrar;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiLanguageInjectionHost;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Injects the shell language into recipes, {@code $(shell ...)} functions and backtick substitutions.
 * <p>
 * A Consulo {@link MultiHostInjector} serves one element class - the JetBrains injector serves three, so
 * {@link MakefileRecipeShellLanguageInjector}, {@link MakefileFunctionShellLanguageInjector} and
 * {@link MakefileSubstitutionShellLanguageInjector} register it once per host type.
 */
public abstract class MakefileShellLanguageInjector implements MultiHostInjector {
    /**
     * The Bash plugin's language - the JetBrains plugin looks up {@code "Shell Script"}.
     */
    private static final String SHELL_LANGUAGE_ID = "Bash";

    private static boolean isTab(char c) {
        return c == '\t';
    }

    @Override
    public void injectLanguages(MultiHostRegistrar registrar, PsiElement context) {
        Language shellLanguage = Language.findLanguageByID(SHELL_LANGUAGE_ID);
        if (shellLanguage == null) {
            return;
        }

        List<TextRange> rangesToInject = new ArrayList<>();
        if (context instanceof MakefileRecipe) {
            for (PsiElement child : context.getChildren()) {
                if (child instanceof MakefileCommand command) {
                    rangesToInject.add(command.getShellCommandRange());
                }
            }
        }
        else if (context instanceof MakefileFunction function) {
            if (function.getFunctionName().textMatches("shell")) {
                for (PsiElement child : context.getChildren()) {
                    if (child instanceof MakefileFunctionParam) {
                        rangesToInject.add(child.getTextRangeInParent());
                    }
                }
            }
        }
        else if (context instanceof MakefileSubstitution) {
            if (context.getTextLength() > 2) {
                rangesToInject.add(new TextRange(1, context.getTextLength() - 1));
            }
        }

        if (!rangesToInject.isEmpty()) {
            registrar.startInjecting(shellLanguage);
            for (TextRange it : rangesToInject) {
                registrar.addPlace(null, null, (PsiLanguageInjectionHost) context, it);
            }
            registrar.doneInjecting();
        }
    }
}
