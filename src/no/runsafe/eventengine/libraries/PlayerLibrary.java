package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class PlayerLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("kill", new Kill());
		lib.set("sendMessage", new SendMessage());
		lib.set("setHealth", new SetHealth());
		lib.set("teleportToLocation", new TeleportToLocation());
		lib.set("teleportToPlayer", new TeleportToPlayer());
		lib.set("strikeLightning", new LightningStrike());

		env.get("engine").set("player", lib);
		return lib;
	}

	static class Kill extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(playerName);
			if ( ObjectLibrary.canEditPlayer(player))
					player.setHealth(0);

			return null;
		}
	}

	static class SendMessage extends TwoArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName, LuaValue message)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(playerName);
			if (ObjectLibrary.canEditPlayer(player))
				player.sendColouredMessage(message.toString());

			return null;
		}
	}

	static class SetHealth extends TwoArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName, LuaValue health)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(playerName);
			if ( ObjectLibrary.canEditPlayer(player))
				player.setHealth(health.toint());
			return null;
		}
	}

	static class TeleportToLocation extends VarArgFunction
	{
		public LuaValue call(LuaValue playerName, LuaValue worldName, LuaValue x, LuaValue y, LuaValue z)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(playerName);
			if ( ObjectLibrary.canEditPlayer(player))
			{
				RunsafeWorld world =  ObjectLibrary.getWorld(worldName);
				if (world != null)
					player.teleport(world, x.todouble(), y.todouble(), z.todouble());
			}

			return null;
		}
	}

	static class TeleportToPlayer extends TwoArgFunction
	{
		public LuaValue call(LuaValue playerName, LuaValue targetPlayerName)
		{
			RunsafePlayer player = ObjectLibrary.getPlayer(playerName);
			RunsafePlayer target = ObjectLibrary.getPlayer(targetPlayerName);

			if ( ObjectLibrary.canEditPlayer(player) &&  ObjectLibrary.canEditPlayer(target))
				player.teleport(player.getLocation());

			return null;
		}
	}

	static class LightningStrike extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(playerName);
			if (ObjectLibrary.canEditPlayer(player))
				player.getWorld().strikeLightningEffect(player.getLocation());

			return null;
		}
	}
}
