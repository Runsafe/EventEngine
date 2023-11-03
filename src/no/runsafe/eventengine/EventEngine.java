package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.ReloadScripts;
import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.handlers.SeatbeltHandler;
import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Events;
import no.runsafe.framework.features.LUAScripts;

public class EventEngine extends RunsafePlugin
{
	public static IDebug Debugger = null;

	@Override
	protected void pluginSetup()
	{
		Debugger = getComponent(IDebug.class);

		addComponent(Events.class);
		addComponent(Commands.class);
		addComponent(LUAScripts.class);

		addComponent(SeatbeltHandler.class);

		addComponent(AILibrary.class);
		addComponent(BossBarLibrary.class);
		addComponent(EffectLibrary.class);
		addComponent(HookingLibrary.class);
		addComponent(PlayerLibrary.class);
		addComponent(WorldLibrary.class);
		addComponent(TimerLibrary.class);
		addComponent(MobLibrary.class);
		addComponent(DebugLibrary.class);

		this.addComponent(ScriptManager.class);
		this.addComponent(HookHandler.class);
		this.addComponent(ReloadScripts.class);
	}
}
