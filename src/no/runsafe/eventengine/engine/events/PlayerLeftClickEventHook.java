package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerClickEvent;
import org.luaj.vm2.LuaTable;

public class PlayerLeftClickEventHook extends AsyncHookInvoker<RunsafePlayerClickEvent>
{
	public PlayerLeftClickEventHook(RunsafePlayerClickEvent event, IScheduler scheduler)
	{
		super(event, scheduler);
		block = event.getBlock();
		blockLocation = block.getLocation();
		blockWorldName = blockLocation.getWorld().getName();
	}

	@Override
	protected boolean filter(Hook hook)
	{
		IWorld world = hook.getWorld();
		return world == null || blockWorldName.equals(world.getName());
	}

	@Override
	protected LuaTable getParameters()
	{
		Item material = block.getMaterial();
		String playerName = event.getPlayer().getName();
		LuaTable table = new LuaTable();
		table.set("player", playerName);
		table.set("world", blockWorldName);
		table.set("x", blockLocation.getBlockX());
		table.set("y", blockLocation.getBlockY());
		table.set("z", blockLocation.getBlockZ());
		table.set("material", block.getMaterial().getName());
		table.set("blockID", material.getItemID());
		table.set("blockData", material.getData());
		return table;
	}

	private final IBlock block;
	private final ILocation blockLocation;
	private final String blockWorldName;
}
