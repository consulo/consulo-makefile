package com.jetbrains.lang.makefile;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.TextAttributesKey;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.colorScheme.setting.ColorDescriptor;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.localize.LocalizeValue;
import consulo.makefile.localize.MakefileLocalize;

import java.util.Map;

@ExtensionImpl
public class MakefileColorSettingsPage implements ColorSettingsPage {
    private static final Map<String, TextAttributesKey> TAGS = Map.of(
        "target", MakefileSyntaxHighlighter.TARGET,
        "specialTarget", MakefileSyntaxHighlighter.SPECIAL_TARGET,
        "variableName", MakefileSyntaxHighlighter.VARIABLE,
        "prerequisite", MakefileSyntaxHighlighter.PREREQUISITE,
        "function", MakefileSyntaxHighlighter.FUNCTION,
        "functionParam", MakefileSyntaxHighlighter.FUNCTION_PARAM
    );

    private static final AttributesDescriptor[] DESCRIPTORS = {
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorComment(), MakefileSyntaxHighlighter.COMMENT),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorDocumentationComment(), MakefileSyntaxHighlighter.DOCCOMMENT),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorKeyword(), MakefileSyntaxHighlighter.KEYWORD),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorTarget(), MakefileSyntaxHighlighter.TARGET),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorSpecialTarget(), MakefileSyntaxHighlighter.SPECIAL_TARGET),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorSeparator(), MakefileSyntaxHighlighter.SEPARATOR),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorPrerequisite(), MakefileSyntaxHighlighter.PREREQUISITE),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorVariableName(), MakefileSyntaxHighlighter.VARIABLE),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorVariableValue(), MakefileSyntaxHighlighter.VARIABLE_VALUE),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorLineSplit(), MakefileSyntaxHighlighter.LINE_SPLIT),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorTab(), MakefileSyntaxHighlighter.TAB),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorFunction(), MakefileSyntaxHighlighter.FUNCTION),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorFunctionParam(), MakefileSyntaxHighlighter.FUNCTION_PARAM),
        new AttributesDescriptor(MakefileLocalize.attributeDescriptorBraces(), MakefileSyntaxHighlighter.BRACES)
    };

    @Override
    public LocalizeValue getDisplayName() {
        return MakefileLanguage.INSTANCE.getDisplayName();
    }

    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public SyntaxHighlighter getHighlighter() {
        return new MakefileSyntaxHighlighter();
    }

    @Override
    public String getDemoText() {
        return """
            # Simple Makefile
            include make.mk

            <target>all</target>: <prerequisite>hello</prerequisite> ## Doc comment

            <target>hello</target>: <prerequisite>hello.o</prerequisite> <prerequisite>world.o</prerequisite>
            <specialTarget>.PHONY: hello</specialTarget>

            <variableName>GCC</variableName> = gcc \\
                       -O2

            <target>.o.c</target>:
            ifeq ($(FOO),'bar')
            \t$(GCC) -c qwe \\
                          -Wall
            else
            \techo "Hello World"
            \t$(<function>error</function><functionParam> Architecture $(ARCH) is not supported</functionParam>)
            endif""";
    }

    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
    }

    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
}
