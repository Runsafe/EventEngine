package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.internal.extension.player.RunsafePlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import org.luaj.vm2.LuaTable;

public class PlayerDeathEventHook extends AsyncHookInvoker<RunsafePlayerDeathEvent>
{
	private final RunsafePlayer player;
	private final IWorld world;

	public PlayerDeathEventHook(RunsafePlayerDeathEvent event, IScheduler scheduler)
	{
		super(event, scheduler);
		player = event.getEntity();
		world = player.getWorld();
	}

	@Override
	protected boolean filter(Hook hook)
	{
		return hook.getWorld().isWorld(world);
	}

	@Override
	protected LuaTable getParameters()
	{
		LuaTable table = new LuaTable();
		table.set("player", player.getName());
		return table;
	}
}
