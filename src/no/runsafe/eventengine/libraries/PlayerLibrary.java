package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.events.CustomEvent;
import no.runsafe.eventengine.engine.EventEngineFunction;
import no.runsafe.eventengine.engine.FunctionParameters;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.ArrayList;
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
		lib.set("cloneInventory", new CloneInventory());
		lib.set("getLocation", new GetLocation());
		lib.set("isDead", new IsDead());
		lib.set("sendEvent", new SendEvent());
		lib.set("getPlayerAtLocation", new GetPlayerAtLocation());
		lib.set("clearInventory", new ClearInventory());
		lib.set("addItem", new AddItem());

		env.get("engine").set("player", lib);
		return lib;
	}

	static class Kill extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			parameters.getPlayer(0).setHealth(0);
			return null;
		}
	}

	static class SendMessage extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			parameters.getPlayer(0).sendColouredMessage(parameters.getString(1));
			return null;
		}
	}

	static class SetHealth extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			parameters.getPlayer(0).setHealth(parameters.getInt(1));
			return null;
		}
	}

	static class TeleportToLocation extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			parameters.getPlayer(0).teleport(parameters.getLocation(1));
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

	static class GetLocation extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafeLocation location = parameters.getPlayer(0).getLocation();
			List<Object> values = new ArrayList<Object>();

			values.add(location.getWorld().getName());
			values.add(location.getX());
			values.add(location.getY());
			values.add(location.getZ());

			return values;
		}
	}

	static class IsDead extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returnValues = new ArrayList<Object>();
			RunsafePlayer player = parameters.getPlayer(0);

			returnValues.add(player.isDead());
			return returnValues;
		}
	}

	static class SendEvent extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			new CustomEvent(parameters.getPlayer(0), parameters.getString(1)).Fire();
			return null;
		}
	}

	static class GetPlayerAtLocation extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returnValues = new ArrayList<Object>();
			RunsafeLocation location = parameters.getLocation(0);

			for (RunsafePlayer player : location.getWorld().getPlayers())
			{
				if (player.getLocation().distance(location) < 2)
				{
					returnValues.add(player.getName());
					return returnValues;
				}
			}
			return returnValues;
		}
	}

	static class ClearInventory extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafePlayer player = parameters.getPlayer(0);
			player.getInventory().clear();
			player.updateInventory();
			return null;
		}
	}

	static class AddItem extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafePlayer player = parameters.getPlayer(0);
			RunsafeMeta meta = Item.get(parameters.getInt(1), parameters.getByte(2)).getItem();
			meta.setAmount(parameters.getInt(3));
			player.getInventory().addItems();
			player.updateInventory();
			return null;
		}
	}
}
