package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.extension.block.RunsafeBlock;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class BlockBreakEventHook extends AsyncHookInvoker<IBlock>
{
	public BlockBreakEventHook(IPlayer player, IBlock block, IScheduler scheduler)
	{
		super(block, scheduler);
		blockLocation = block.getLocation();
		blockWorld = blockLocation.getWorld().getName();
		this.player = player;
	}

	@Override
	protected boolean filter(Hook hook)
	{
		IWorld world = hook.getWorld();
		return world == null || blockWorld.equals(world.getName());
	}

	@Override
	protected LuaTable getParameters()
	{
		LuaTable table = new LuaTable();
		if (player != null) table.set("player", LuaValue.valueOf(player.getName()));

		table.set("world", blockWorld);
		table.set("x", blockLocation.getBlockX());
		table.set("y", blockLocation.getBlockY());
		table.set("z", blockLocation.getBlockZ());
		table.set("material", event.getMaterial().getName());
		table.set("blockID", event.getMaterial().getItemID());
		table.set("blockData", ((RunsafeBlock) event).getData());

		return table;
	}

	private final ILocation blockLocation;
	private final String blockWorld;
	private final IPlayer player;
}
