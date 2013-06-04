package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.output.IOutput;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RunScript.class);
		Plugin.console = this.output;

		LuaValue _G = JsePlatform.standardGlobals();
		_G.load(new Engine());
	}

	public static IOutput console;
	public static Globals global;
}
