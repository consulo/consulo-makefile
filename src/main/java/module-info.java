/**
 * @author VISTALL
 * @since 09/01/2023
 */
module consulo.makefile
{
    requires consulo.ide.api;

    exports com.advancedtools.cpp.makefile;
    exports com.advancedtools.cpp.makefile.lang;
    exports com.advancedtools.cpp.makefile.psi;
    exports consulo.makefile;
    exports consulo.makefile.codeInsight;
    exports consulo.makefile.codeInsight.completion;
    exports consulo.makefile.icon;
}