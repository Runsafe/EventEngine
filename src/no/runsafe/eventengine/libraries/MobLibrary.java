package no.runsafe.eventengine.libraries;

import net.minecraft.server.v1_12_R1.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.minecraft.entity.PassiveEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeItemFrame;
import no.runsafe.framework.minecraft.entity.RunsafeMinecart;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

public class MobLibrary extends Library
{
	public MobLibrary(RunsafePlugin plugin)
	{
		super(plugin, "mobs");
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("spawnEntity", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				return no.runsafe.framework.minecraft.entity.EntityType.getTypeByName(parameters.getString(0)).spawn(
					parameters.getLocation(1)).getEntityId();
			}
		});

		lib.set("getEntity", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				ILocation location = parameters.getLocation(0);
				double lastDistance = -1;
				RunsafeEntity lastEntity = null;

				for (RunsafeEntity entity : location.getChunk().getEntities())
				{
					ILocation entityLocation = entity.getLocation();
					if (entityLocation == null)
						continue;

					double entityDistance = entityLocation.distance(location);
					if (lastDistance == -1 || entityDistance < lastDistance)
					{
						lastDistance = entityDistance;
						lastEntity = entity;
					}
				}
				return lastEntity != null ? lastEntity.getEntityId() : 0;
			}
		});

		lib.set("isAlive", new BooleanFunction()
		{
			@Override
			protected boolean run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				return entity != null && !entity.isDead();
			}
		});

		/*
		 * Spawns a custom minecart with a block in it.
		 * @param 0 Block value of block to sit in minecart.
		 * @param 1 Data value of block to sit in minecart.
		 * @param 2 Block offset of block to sit in minecart.
		 */
		lib.set("spawnCustomMinecart", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				//Create minecart
				RunsafeMinecart minecart = (RunsafeMinecart) PassiveEntity.Minecart.spawn(parameters.getLocation(0));

				//Create block in minecart
				minecart.setDisplayBlock(
					new org.bukkit.material.MaterialData(
						parameters.getInt(0),
						parameters.getByte(1)
					)
				);

				//Set block offset
				if (parameters.getInt(2) != null)
					minecart.setDisplayBlockOffset(parameters.getInt(2));

				return minecart.getEntityId();
			}
		});

		lib.set("despawnEntity", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null)
					entity.remove();
			}
		});

		lib.set("setItem", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null && entity.getEntityType() == PassiveEntity.ItemFrame)
				{
					RunsafeItemFrame itemFrame = (RunsafeItemFrame) entity;
					String itemName = parameters.getString(2);
					no.runsafe.framework.minecraft.Item item = no.runsafe.framework.minecraft.Item.get(itemName);
					if (item == null)
					{
						throw new LuaError("Script specified an invalid item name " + itemName);
					}
					itemFrame.setItem(item.getItem());
				}
			}
		});

		lib.set("addEffect", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				int entityId = parameters.getInt(1);
				IEntity entity = parameters.getWorld(0).getEntityById(entityId);
				if (entity != null)
				{
					EntityLiving living = (EntityLiving) ObjectUnwrapper.getMinecraft(entity);
					if (living == null)
					{
						throw new LuaError("Entity with id " + entityId + " does not exist");
					}
					living.addEffect(new MobEffect(
						MobEffectList.fromId(parameters.getInt(2)),
						parameters.getInt(3),
						parameters.getInt(4),
						parameters.getBool(5),
						true
					));
				}
			}
		});

		lib.set("putPlayerOn", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null)
					entity.setPassenger(parameters.getPlayer(2));
			}
		});

		lib.set("putOnPlayer", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null)
					parameters.getPlayer(2).setPassenger(entity);
			}
		});

		lib.set("dismount", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null)
					entity.leaveVehicle();
			}
		});

		return lib;
	}
}
