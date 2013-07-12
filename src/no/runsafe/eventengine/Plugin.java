package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.ReloadScripts;
import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.lua.LuaEnvironment;
import no.runsafe.moosic.MusicHandler;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(getPluginAPI(MusicHandler.class));
		Plugin.console = this.output;

		LuaEnvironment.global.load(new HookingLibrary());
		LuaEnvironment.global.load(new PlayerLibrary());
		LuaEnvironment.global.load(new EffectLibrary());
		LuaEnvironment.global.load(new WorldLibrary());
		LuaEnvironment.global.load(new SoundLibrary());
		LuaEnvironment.global.load(new AILibrary());

		SoundLibrary.musicHandler = this.getComponent(no.runsafe.moosic.MusicHandler.class);
		this.addComponent(ScriptManager.class);
		this.addComponent(HookHandler.class);
		this.addComponent(ReloadScripts.class);
	}

	public static IOutput console;
}
