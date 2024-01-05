package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerChatEvent;
import org.luaj.vm2.LuaTable;

public class PlayerChatEventHook extends AsyncHookInvoker<RunsafePlayerChatEvent>
{
	public PlayerChatEventHook(RunsafePlayerChatEvent event, IPlayer player, IScheduler scheduler)
	{
		super(event, scheduler);
		this.player = player;
		playerWorld = player.getWorld();
		message = event.getMessage();
	}

	@Override
	protected boolean filter(Hook hook)
	{
		if (!(hook.getWorld().isWorld(playerWorld)))
		{
			EventEngine.Debugger.debugFiner("Filtering chat event: Wrong world.");
			return false;
		}
		EventEngine.Debugger.debugFiner("Filtering chat event: Correct world");
		return true;
	}

	@Override
	protected LuaTable getParameters()
	{
		LuaTable table = new LuaTable();
		table.set("player", player.getName());
		table.set("message", message);
		return table;
	}

	private final IPlayer player;
	private final IWorld playerWorld;
	private final String message;
}
