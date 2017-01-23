package consulo.makefile;

import org.jetbrains.annotations.PropertyKey;
import com.intellij.AbstractBundle;

/**
 * @author VISTALL
 * @since 18:40/16.03.13
 */
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
