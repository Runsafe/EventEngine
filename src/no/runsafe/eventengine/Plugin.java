package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.eventengine.functions.boobs;
import no.runsafe.framework.RunsafePlugin;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RunScript.class);

		LuaValue global = JsePlatform.standardGlobals();
		global.load(new boobs());
	}
}
