package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.LuaEnv;
import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.eventengine.engine.Engine;
import no.runsafe.eventengine.engine.ScriptRunner;
import no.runsafe.eventengine.libraries.PlayerLibrary;
import no.runsafe.eventengine.libraries.SoundLibrary;
import no.runsafe.eventengine.triggers.RedstoneTriggers;
import no.runsafe.eventengine.triggers.TriggerHandler;
import no.runsafe.eventengine.triggers.TriggerRepository;
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
		this.addComponent(ScriptRunner.class);
		this.addComponent(TriggerRepository.class);
		this.addComponent(TriggerHandler.class);
		this.addComponent(RedstoneTriggers.class);
		this.addComponent(LuaEnv.class);

		this.addComponent(getPluginAPI(MusicHandler.class));
		this.addComponent(RunScript.class);
		Plugin.console = this.output;

		LuaValue _G = JsePlatform.standardGlobals();
		_G.load(new Engine());

		SoundLibrary.musicHandler = this.getComponent(no.runsafe.moosic.MusicHandler.class);
		PlayerLibrary.achievementFinder = this.getComponent(no.runsafe.cheeves.AchievementFinder.class);
	}

	public static IOutput console;
}
