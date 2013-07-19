package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.events.CustomEvent;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

public class PlayerLibrary extends Library
{
	public PlayerLibrary(RunsafePlugin plugin)
	{
		super(plugin, "player");
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("kill", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).setHealth(0.0D);
			}
		});
		lib.set("getLocation", new LocationFunction()
		{
			@Override
			public RunsafeLocation run(FunctionParameters parameters)
			{
				return parameters.getPlayer(0).getLocation();
			}
		});
		lib.set("isDead", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				return parameters.getPlayer(0).isDead();
			}
		});

		lib.set("sendMessage", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).sendColouredMessage(parameters.getString(1));
			}
		});
		lib.set("setHealth", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).setHealth(parameters.getDouble(1));
			}
		});
		lib.set("teleportToLocation", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).teleport(parameters.getLocation(1));
			}
		});
		lib.set("teleportToLocationRotation", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).teleport(parameters.getLocation(1, true));
			}
		});
		lib.set("teleportToPlayer", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).teleport(parameters.getPlayer(1));
			}
		});
		lib.set("cloneInventory", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				CloneInventory(parameters.getPlayer(0), parameters.getPlayer(1));
			}
		});
		lib.set("sendEvent", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				new CustomEvent(parameters.getPlayer(0), parameters.getString(1)).Fire();
			}
		});
		lib.set("clearInventory", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).clearInventory();
			}
		});

		lib.set("addItem", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				AddItem(parameters.getPlayer(0), parameters.getInt(1), parameters.getByte(2), parameters.getInt(3));
			}
		});
		lib.set("getPlayerAtLocation", new StringFunction()
		{
			@Override
			public String run(FunctionParameters parameters)
			{
				return GetPlayerAtLocation(parameters.getLocation(0));
			}
		});

		return lib;
	}

	private static void CloneInventory(RunsafePlayer source, RunsafePlayer target)
	{
		target.getInventory().unserialize(source.getInventory().serialize());
		target.updateInventory();
	}

	private static String GetPlayerAtLocation(RunsafeLocation location)
	{
		for (RunsafePlayer player : location.getWorld().getPlayers())
			if (player.getLocation().distance(location) < 2)
				return player.getName();
		return null;
	}

	private static void AddItem(RunsafePlayer player, int itemId, byte data, int amount)
	{
		RunsafeMeta meta = Item.get(itemId, data).getItem();
		meta.setAmount(amount);
		player.give(meta);
	}
}
