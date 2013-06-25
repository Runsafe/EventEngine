package no.runsafe.eventengine;

import no.runsafe.eventengine.engine.Engine;
import no.runsafe.eventengine.engine.ScriptRunner;
import no.runsafe.eventengine.libraries.SoundLibrary;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.moosic.MusicHandler;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(getPluginAPI(MusicHandler.class));
		Plugin.console = this.output;

		LuaValue _G = JsePlatform.standardGlobals();
		_G.load(new Engine());

		SoundLibrary.musicHandler = this.getComponent(no.runsafe.moosic.MusicHandler.class);

		this.addComponent(ScriptRunner.class);
	}

	public static IOutput console;
}
