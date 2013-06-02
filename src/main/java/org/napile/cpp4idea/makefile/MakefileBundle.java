package org.napile.cpp4idea.makefile;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;
import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;

/**
 * @author VISTALL
 * @since 18:40/16.03.13
 */
public class MakefileBundle
{
	private static Reference<ResourceBundle> ourBundle;

	@NonNls
	private static final String BUNDLE = "org.napile.cpp4idea.makefile.MakefileBundle";

	private MakefileBundle()
	{
	}

	public static String message(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params)
	{
		return CommonBundle.message(getBundle(), key, params);
	}

	private static ResourceBundle getBundle()
	{
		ResourceBundle bundle = null;
		if(ourBundle != null)
			bundle = ourBundle.get();
		if(bundle == null)
		{
			bundle = ResourceBundle.getBundle(BUNDLE);
			ourBundle = new SoftReference<ResourceBundle>(bundle);
		}
		return bundle;
	}

}
