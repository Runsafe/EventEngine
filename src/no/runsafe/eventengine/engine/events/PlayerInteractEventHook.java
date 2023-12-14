package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerInteractEvent;
import org.luaj.vm2.LuaTable;

public class PlayerInteractEventHook extends AsyncHookInvoker<RunsafePlayerInteractEvent>
{
	public PlayerInteractEventHook(RunsafePlayerInteractEvent event, IScheduler scheduler)
	{
		super(event, scheduler);
		block = event.getBlock();
		if (block != null)
			location = block.getLocation();
		else
			location = null;
	}

	@Override
	protected boolean filter(Hook hook)
	{
		if (hook.getData() != null)
		{
			if (block == null)
			{
				EventEngine.Debugger.debugFiner("block is null, ignoring event...");
				return false;
			}
			int blockItemId = block.getMaterial().getItemID();
			int hookItemId = (int) hook.getData();
			if (blockItemId != hookItemId)
			{
				EventEngine.Debugger.debugFiner(
					"Item id %d of block does not match hook registration for %d, ignoring event...",
					blockItemId, hookItemId
				);
				return false;
			}
		}

		EventEngine.Debugger.debugFiner("Block is not null");

		IWorld hookWorld = hook.getWorld();
		ILocation location = block.getLocation();
		if (hookWorld == null)
		{
			EventEngine.Debugger.debugFiner("Hook world is null, ignoring");
		}
		if (!block.getWorld().isWorld(hookWorld))
		{
			EventEngine.Debugger.debugFine("Wrong world!");
			return false;
		}
		if (location.distance(hook.getLocation()) >= 1)
		{
			EventEngine.Debugger.debugFiner("Wrong location");
			return false;
		}
		return true;
	}

	@Override
	protected LuaTable getParameters()
	{
		LuaTable table = new LuaTable();
		if (event.getPlayer() != null)
		{
			table.set("player", event.getPlayer().getName());
		}
		if (location == null)
			return table;

		table.set("world", location.getWorld().getName());
		table.set("x", location.getBlockX());
		table.set("y", location.getBlockY());
		table.set("z", location.getBlockZ());
		table.set("material", block.getMaterial().getName());
		table.set("blockID", block.getMaterial().getItemID());
		table.set("blockData", (block).getData());
		return table;
	}

	private final IBlock block;
	private final ILocation location;
}
