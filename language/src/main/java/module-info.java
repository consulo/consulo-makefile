/**
 * @author VISTALL
 * @since 09/01/2023
 */
// not @NullMarked: the Grammar-Kit and JFlex sources generated into the same packages are not null-safe
module consulo.makefile {
    requires consulo.annotation;
    requires consulo.application.api;
    requires consulo.application.content.api;
    requires consulo.code.editor.api;
    requires consulo.color.scheme.api;
    requires consulo.component.api;
    requires consulo.configurable.api;
    requires consulo.document.api;
    requires consulo.execution.api;
    requires consulo.file.editor.api;
    requires consulo.file.template.api;
    requires consulo.ide.api;
    requires consulo.index.io;
    requires consulo.language.api;
    requires consulo.language.code.style.api;
    requires consulo.language.code.style.ui.api;
    requires consulo.language.editor.api;
    requires consulo.language.impl;
    requires consulo.language.spellchecker.api;
    requires consulo.localize.api;
    requires consulo.navigation.api;
    requires consulo.project.api;
    requires consulo.project.content.api;
    requires consulo.ui.api;
    requires consulo.util.collection;
    requires consulo.util.lang;
    requires consulo.virtual.file.system.api;

    exports com.jetbrains.lang.makefile;
    exports com.jetbrains.lang.makefile.inspections;
    exports com.jetbrains.lang.makefile.psi;
    exports com.jetbrains.lang.makefile.psi.impl;
    exports com.jetbrains.lang.makefile.stub;
    exports consulo.makefile.icon;
    exports consulo.makefile.localize;
}
