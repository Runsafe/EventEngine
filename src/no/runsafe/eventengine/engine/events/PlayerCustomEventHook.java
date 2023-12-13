package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import org.luaj.vm2.LuaTable;

public class PlayerCustomEventHook extends AsyncHookInvoker<RunsafeCustomEvent>
{
	private final String world;
	private final String region;

	public PlayerCustomEventHook(RunsafeCustomEvent event, String world, String region, IScheduler scheduler)
	{
		super(event, scheduler);
		this.world = world;
		this.region = region;
	}

	@Override
	protected boolean filter(Hook hook)
	{
		return ((String) hook.getData()).equalsIgnoreCase(String.format("%s-%s", world, region));
	}

	@Override
	protected LuaTable getParameters()
	{
		final LuaTable table = new LuaTable();
		table.set("player", event.getPlayer().getName());
		table.set("world", world);
		table.set("region", region);
		return table;
	}
}
