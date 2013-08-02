package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.ReloadScripts;
import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IScheduler;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.scheduler = this.getComponent(IScheduler.class);
		addComponent(AILibrary.class);
		addComponent(EffectLibrary.class);
		addComponent(HookingLibrary.class);
		addComponent(PlayerLibrary.class);
		addComponent(WorldLibrary.class);
		addComponent(TimerLibrary.class);

		this.addComponent(ScriptManager.class);
		this.addComponent(HookHandler.class);
		this.addComponent(ReloadScripts.class);
	}
	public IScheduler scheduler;
}
