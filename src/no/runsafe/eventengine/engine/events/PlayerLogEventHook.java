package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.player.IPlayer;
import org.luaj.vm2.LuaTable;

public class PlayerLogEventHook extends AsyncHookInvoker<IPlayer>
{
	private final String player;

	@SuppressWarnings("unused")
	public PlayerLogEventHook(IPlayer player, IScheduler scheduler)
	{
		super(player, scheduler);
		this.player = player.getName();
	}

	@Override
	protected boolean filter(Hook hook)
	{
		return hook.getPlayerName().equalsIgnoreCase(player);
	}

	@Override
	protected LuaTable getParameters()
	{
		return null;
	}
}
