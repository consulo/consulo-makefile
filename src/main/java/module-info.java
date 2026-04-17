/**
 * @author VISTALL
 * @since 09/01/2023
 */
module consulo.makefile
{
    requires consulo.application.api;
    requires consulo.code.editor.api;
    requires consulo.color.scheme.api;
    requires consulo.component.api;
    requires consulo.document.api;
    requires consulo.file.editor.api;
    requires consulo.language.api;
    requires consulo.language.editor.api;
    requires consulo.language.impl;
    requires consulo.localize.api;
    requires consulo.navigation.api;
    requires consulo.ui.api;
    requires consulo.util.dataholder;
    requires consulo.util.lang;
    requires consulo.virtual.file.system.api;

    exports com.advancedtools.cpp.makefile;
    exports com.advancedtools.cpp.makefile.lang;
    exports com.advancedtools.cpp.makefile.psi;
    exports consulo.makefile;
    exports consulo.makefile.codeInsight;
    exports consulo.makefile.codeInsight.completion;
    exports consulo.makefile.icon;
    exports consulo.makefile.localize;
}
