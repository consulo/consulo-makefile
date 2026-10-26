/**
 * The plugin: the Makefile language and the Make import. The C/C++ part, consulo.makefile.cpp, is loaded only with the
 * C/C++ plugin.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
module consulo.makefile.impl {
    requires consulo.makefile;
    requires consulo.makefile.external.system;
}
