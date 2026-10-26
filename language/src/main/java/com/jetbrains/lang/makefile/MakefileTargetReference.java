package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileElementFactory;
import com.jetbrains.lang.makefile.psi.MakefilePrerequisite;
import com.jetbrains.lang.makefile.psi.MakefileRule;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import consulo.language.ast.ASTNode;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementResolveResult;
import consulo.language.psi.PsiPolyVariantReferenceBase;
import consulo.language.psi.ResolveResult;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakefileTargetReference extends PsiPolyVariantReferenceBase<MakefilePrerequisite> {
    private static final Pattern VARIABLE_USAGE = Pattern.compile("\\$\\((.*)\\)");

    private final MakefilePrerequisite myPrerequisite;

    public MakefileTargetReference(MakefilePrerequisite prerequisite) {
        super(prerequisite, false);
        myPrerequisite = prerequisite;
    }

    @Override
    public PsiElement handleElementRename(String newName) {
        if (newName.contains("%")) {
            return myPrerequisite;
        }
        ASTNode identifierNode = myPrerequisite.getNode().getFirstChildNode();
        if (identifierNode != null) {
            MakefileTarget target = MakefileElementFactory.createTarget(myPrerequisite.getProject(), newName);
            ASTNode newIdentifierNode = target.getFirstChild().getNode();
            myPrerequisite.getNode().replaceChild(identifierNode, newIdentifierNode);
        }
        return myPrerequisite;
    }

    public @Nullable MakefileRule getRule() {
        return myPrerequisite.getParent().getParent().getParent().getParent() instanceof MakefileRule rule ? rule : null;
    }

    @Override
    public Object[] getVariants() {
        MakefileRule rule = getRule();
        Map<String, MakefileTarget> distinct = new LinkedHashMap<>();
        for (MakefileTarget it : ((MakefileFile) myPrerequisite.getContainingFile()).getTargets()) {
            if (it.isPatternTarget()) {
                continue;
            }
            if (rule != null && rule.getTargets().stream().anyMatch(t -> t.getName().equals(it.getName()))) {
                continue;
            }
            distinct.putIfAbsent(it.getName(), it);
        }
        return distinct.values().stream()
            .map(it -> LookupElementBuilder.create(it).withIcon(MakefileUtil.MAKEFILE_TARGET_ICON))
            .toArray();
    }

    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Matcher match = VARIABLE_USAGE.matcher(myPrerequisite.getText());
        if (match.find()) {
            String name = match.group(1);
            return ((MakefileFile) myPrerequisite.getContainingFile()).getVariables().stream()
                .filter(it -> it.getText().equals(name))
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new);
        }
        return ((MakefileFile) myPrerequisite.getContainingFile()).getAllTargets().stream()
            .filter(it -> it.matches(myPrerequisite.getText()))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return element instanceof MakefileTarget target && myPrerequisite.getText().equals(target.getName());
    }
}
