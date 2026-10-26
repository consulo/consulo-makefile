package com.jetbrains.lang.makefile;

import com.jetbrains.lang.makefile.psi.MakefileRule;
import com.jetbrains.lang.makefile.psi.MakefileTarget;
import com.jetbrains.lang.makefile.psi.MakefileVariable;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.virtualFileSystem.fileType.FileType;

import java.util.Collection;
import java.util.List;

public class MakefileFile extends PsiFileBase {
    public MakefileFile(FileViewProvider viewProvider) {
        super(viewProvider, MakefileLanguage.INSTANCE);
    }

    @Override
    public FileType getFileType() {
        return MakefileFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Makefile";
    }

    public Collection<MakefileRule> getRules() {
        return PsiTreeUtil.findChildrenOfType(this, MakefileRule.class);
    }

    public Collection<MakefileTarget> getAllTargets() {
        return PsiTreeUtil.findChildrenOfType(this, MakefileTarget.class);
    }

    public Collection<MakefileTarget> getTargets() {
        return getAllTargets().stream().filter(it -> !it.isSpecialTarget()).toList();
    }

    public Collection<MakefileTarget> getSpecialTargets() {
        return getAllTargets().stream().filter(MakefileTarget::isSpecialTarget).toList();
    }

    public Collection<MakefileRule> getPhonyRules() {
        return getRules().stream().filter(it -> {
            List<MakefileTarget> targets = it.getTargets();
            return targets.size() == 1 && targets.get(0).getName().equals(".PHONY");
        }).toList();
    }

    public Collection<MakefileVariable> getVariables() {
        return PsiTreeUtil.findChildrenOfType(this, MakefileVariable.class);
    }
}
