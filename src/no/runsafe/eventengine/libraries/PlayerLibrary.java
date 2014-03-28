package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.events.CustomEvent;
import no.runsafe.eventengine.handlers.SeatbeltHandler;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.LegacyMaterial;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.worldguardbridge.IRegionControl;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;

public class PlayerLibrary extends Library
{
	public PlayerLibrary(RunsafePlugin plugin, IScheduler scheduler, IRegionControl regionControl)
	{
		super(plugin, "player");
		this.scheduler = scheduler;
		this.regionControl = regionControl;
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
			public ILocation run(FunctionParameters parameters)
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
		lib.set("isOnline", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				return parameters.getPlayer(0).isOnline();
			}
		});

		lib.set("addPermission", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).addPermission(parameters.getString(1));
			}
		});

		lib.set("addWorldPermission", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).addPermission(parameters.getString(1), parameters.getString(2));
			}
		});

		lib.set("removePermission", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).removePermission(parameters.getString(1));
			}
		});

		lib.set("removeWorldPermission", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).removePermission(parameters.getString(1), parameters.getString(2));
			}
		});

		lib.set("removePotionEffects", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).removeBuffs();
			}
		});

		lib.set("closeInventory", new VoidFunction()
		{
			@Override
			protected void run(final FunctionParameters parameters)
			{
				scheduler.startSyncTask(new Runnable()
				{
					@Override
					public void run()
					{
						parameters.getPlayer(0).closeInventory();
					}
				}, 1L);
			}
		});

		lib.set("setVelocity", new VoidFunction()
		{
			@Override
			protected void run(final FunctionParameters parameters)
			{
				parameters.getPlayer(0).setVelocity(new Vector(
						parameters.getDouble(1),
						parameters.getDouble(2),
						parameters.getDouble(3)
				));
			}
		});

		lib.set("lockMount", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				SeatbeltHandler.lockPlayer(parameters.getPlayer(0));
			}
		});

		lib.set("unlockMount", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				SeatbeltHandler.unlockPlayer(parameters.getPlayer(0));
			}
		});

		lib.set("dismount", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getPlayer(0).leaveVehicle();
			}
		});

		lib.set("isInRegion", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				String checkRegion = parameters.getString(1) + '-' + parameters.getString(2);
				return regionControl.getApplicableRegions(parameters.getPlayer(0)).contains(checkRegion);
			}
		});

		lib.set("hasItem", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				return parameters.getPlayer(0).hasItem(Item.get(parameters.getString(1)), parameters.getInt(2));
			}
		});

		lib.set("hasItemWithName", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				String requiredName = parameters.getString(1);
				for (RunsafeMeta item : parameters.getPlayer(0).getInventory().getContents())
				{
					String displayName = item.getDisplayName();
					if (displayName != null && displayName.equals(requiredName))
						return true;
				}
				return false;
			}
		});

		return lib;
	}

	private static void CloneInventory(IPlayer source, IPlayer target)
	{
		target.getInventory().unserialize(source.getInventory().serialize());
		target.updateInventory();
	}

	private static String GetPlayerAtLocation(ILocation location)
	{
		for (IPlayer player : location.getWorld().getPlayers())
			if (player.getLocation().distance(location) < 2)
				return player.getName();
		return null;
	}

	private static void AddItem(IPlayer player, int itemId, byte data, int amount)
	{
		RunsafeMeta meta = Item.get(LegacyMaterial.getById(itemId), data).getItem();
		meta.setAmount(amount);
		player.give(meta);
	}

	private final IScheduler scheduler;
	private final IRegionControl regionControl;
}
