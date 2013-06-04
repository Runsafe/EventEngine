package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.engine.EventEngineFunction;
import no.runsafe.eventengine.engine.FunctionParameters;
import no.runsafe.eventengine.objects.LuaPlayer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.List;

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
		lib.set("cloneInventory", new CloneInventory());

		env.get("engine").set("player", lib);
		return lib;
	}

	static class Kill extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			if (parameters.isPlayer(0))
				parameters.getPlayer(0).setHealth(0);

			return null;
		}
	}

	static class SendMessage extends TwoArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName, LuaValue message)
		{
			LuaPlayer playerWrapper = new LuaPlayer(playerName);

			if (playerWrapper.isPlayer())
				playerWrapper.player().sendColouredMessage(message.toString());

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
		public Varargs invoke(Varargs args)
		{
			RunsafePlayer player =  ObjectLibrary.getPlayer(args.checkstring(1));
			if ( ObjectLibrary.canEditPlayer(player))
			{
				RunsafeWorld world =  ObjectLibrary.getWorld(args.checkstring(2));
				if (world != null)
					player.teleport(world, args.checkdouble(3), args.checkdouble(4), args.checkdouble(5));
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
				player.teleport(target.getLocation());

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

	static class CloneInventory extends TwoArgFunction
	{
		@Override
		public LuaValue call(LuaValue sourcePlayer, LuaValue targetPlayer)
		{
			RunsafePlayer source = ObjectLibrary.getPlayer(sourcePlayer);
			RunsafePlayer target = ObjectLibrary.getPlayer(targetPlayer);

			if (ObjectLibrary.canEditPlayer(source) && ObjectLibrary.canEditPlayer(target))
			{
				target.getInventory().unserialize(source.getInventory().serialize());
				target.updateInventory();
			}
			return null;
		}
	}
}
