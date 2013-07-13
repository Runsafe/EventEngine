package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.ReloadScripts;
import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.RunsafePlugin;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		addComponent(AILibrary.class);
		addComponent(EffectLibrary.class);
		addComponent(HookingLibrary.class);
		addComponent(PlayerLibrary.class);
		addComponent(WorldLibrary.class);

		this.addComponent(ScriptManager.class);
		this.addComponent(HookHandler.class);
		this.addComponent(ReloadScripts.class);
	}
}
