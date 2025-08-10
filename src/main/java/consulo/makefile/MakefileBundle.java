package consulo.makefile;

import consulo.annotation.DeprecationInfo;
import consulo.annotation.internal.MigratedExtensionsTo;
import consulo.component.util.localize.AbstractBundle;
import consulo.makefile.localize.MakefileLocalize;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author VISTALL
 * @since 18:40/16.03.13
 */
@Deprecated
@DeprecationInfo("Use Localize")
@MigratedExtensionsTo(MakefileLocalize.class)
public class MakefileBundle extends AbstractBundle
{
	private static final MakefileBundle ourInstance = new MakefileBundle();

	private MakefileBundle()
	{
		super("messages.MakefileBundle");
	}

	public static String message(@PropertyKey(resourceBundle = "messages.MakefileBundle") String key)
	{
		return ourInstance.getMessage(key);
	}

	public static String message(@PropertyKey(resourceBundle = "messages.MakefileBundle") String key, Object... params)
	{
		return ourInstance.getMessage(key, params);
	}
}
