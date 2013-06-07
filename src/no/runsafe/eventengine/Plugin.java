package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.eventengine.engine.Engine;
import no.runsafe.eventengine.libraries.SoundLibrary;
import no.runsafe.eventengine.triggers.RedstoneTriggers;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.output.IOutput;
import no.runsafe.moosic.MusicHandler;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RedstoneTriggers.class);
		this.addComponent(getPluginAPI(MusicHandler.class));
		this.addComponent(RunScript.class);
		Plugin.console = this.output;

		LuaValue _G = JsePlatform.standardGlobals();
		_G.load(new Engine());

		SoundLibrary.musicHandler = this.getComponent(no.runsafe.moosic.MusicHandler.class);
	}

	public static IOutput console;
}
