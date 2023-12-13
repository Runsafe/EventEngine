package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import org.luaj.vm2.LuaTable;

public class PlayerDamageEventHook extends AsyncHookInvoker<RunsafeEntityDamageEvent>
{
	public PlayerDamageEventHook(RunsafeEntityDamageEvent event, IPlayer player, IScheduler scheduler)
	{
		super(event, scheduler);
		damageWorld = player.getWorld();
		this.player = player;
	}

	@Override
	protected boolean filter(Hook hook)
	{
		IWorld world = hook.getWorld();
		return world != null && world.isWorld(damageWorld);
	}

	@Override
	protected LuaTable getParameters()
	{
		LuaTable table = new LuaTable();
		table.set("player", player.getName());
		table.set("playerHealth", player.getHealth());
		table.set("playerMaxHealth", player.getMaxHealth());
		table.set("damage", event.getDamage());
		table.set("cause", event.getCause().name());
		return table;
	}

	private final IWorld damageWorld;
	private final IPlayer player;
}
