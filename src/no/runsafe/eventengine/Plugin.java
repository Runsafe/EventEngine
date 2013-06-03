package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.eventengine.functions.player;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.output.IOutput;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.Console;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RunScript.class);
		Plugin.console = this.output;

		LuaValue global = JsePlatform.standardGlobals();
		global.load(new player());
	}

	public static IOutput console;
}
