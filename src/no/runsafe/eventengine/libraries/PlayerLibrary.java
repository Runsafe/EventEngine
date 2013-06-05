package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.engine.EventEngineFunction;
import no.runsafe.eventengine.engine.FunctionParameters;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

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

	static class SendMessage extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			if (parameters.isPlayer(0))
				parameters.getPlayer(0).sendColouredMessage(parameters.getString(1));

			return null;
		}
	}

	static class SetHealth extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			if (parameters.isPlayer(0))
				parameters.getPlayer(0).setHealth(parameters.getInt(1));

			return null;
		}
	}

	static class TeleportToLocation extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			if (parameters.isPlayer(0))
			{
				RunsafePlayer player = parameters.getPlayer(0);
				player.teleport(parameters.getLocation(1));
			}
			return null;
		}
	}

	static class TeleportToPlayer extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			parameters.getPlayer(0).teleport(parameters.getPlayer(1).getLocation());
			return null;
		}
	}

	static class LightningStrike extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafePlayer player = parameters.getPlayer(0);
			player.getWorld().strikeLightningEffect(player.getLocation());
			return null;
		}
	}

	static class CloneInventory extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafePlayer source = parameters.getPlayer(0);
			RunsafePlayer target = parameters.getPlayer(1);

			target.getInventory().unserialize(source.getInventory().serialize());
			target.updateInventory();
			return null;
		}
	}
}
