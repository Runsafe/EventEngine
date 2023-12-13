package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockRedstoneEvent;
import org.luaj.vm2.LuaTable;

public class BlockRedstoneEventHook extends AsyncHookInvoker<RunsafeBlockRedstoneEvent>
{
	public BlockRedstoneEventHook(RunsafeBlockRedstoneEvent event, IScheduler scheduler)
	{
		super(event, scheduler);
		location = event.getBlock().getLocation();
		world = location.getWorld();
	}

	@Override
	protected boolean filter(Hook hook)
	{
		return hook.getWorld().isWorld(world) && location.distance(hook.getLocation()) < 1;
	}

	@Override
	protected LuaTable getParameters()
	{
		return null;
	}

	private final ILocation location;
	private final IWorld world;
}
