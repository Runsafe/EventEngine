package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.ReloadScripts;
import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.moosic.MusicHandler;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(getPluginAPI(MusicHandler.class));
		Plugin.console = this.output;

		addComponent(AILibrary.class);
		addComponent(EffectLibrary.class);
		addComponent(HookingLibrary.class);
		addComponent(PlayerLibrary.class);
		addComponent(SoundLibrary.class);
		addComponent(WorldLibrary.class);

		SoundLibrary.musicHandler = this.getComponent(no.runsafe.moosic.MusicHandler.class);
		this.addComponent(ScriptManager.class);
		this.addComponent(HookHandler.class);
		this.addComponent(ReloadScripts.class);
	}

	private static IOutput console;
}
