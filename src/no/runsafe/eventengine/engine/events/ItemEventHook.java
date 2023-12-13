package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.CancellableEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import org.luaj.vm2.LuaTable;

public class ItemEventHook extends AsyncHookInvoker<CancellableEvent>
{
	public ItemEventHook(CancellableEvent event, IPlayer player, RunsafeMeta item, IScheduler scheduler)
	{
		super(event, scheduler);
		world = player.getWorld();
		playerName = player.getName();
		this.item = item;
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
		table.set("player", playerName);
		table.set("material", item.getType().name());
		table.set("itemID", item.getItemId());
		table.set("itemName", item.hasDisplayName() ? item.getDisplayName() : item.getNormalName());
		return table;
	}

	private final IWorld world;
	private final String playerName;
	private final RunsafeMeta item;
}
