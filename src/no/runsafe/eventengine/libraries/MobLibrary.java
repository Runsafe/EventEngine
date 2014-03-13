package no.runsafe.eventengine.libraries;

import net.minecraft.server.v1_7_R1.*;
import no.runsafe.eventengine.entity.EntityDriver;
import no.runsafe.eventengine.entity.PathfinderGoToTarget;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.minecraft.entity.PassiveEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.util.UnsafeList;
import org.luaj.vm2.LuaTable;

import java.lang.reflect.Field;

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
				return no.runsafe.framework.minecraft.entity.EntityType.getTypeByName(parameters.getString(0)).spawn(parameters.getLocation(1)).getEntityId();
			}
		});

		lib.set("spawnCustomMinecart", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				IEntity entity = PassiveEntity.Minecart.spawn(parameters.getLocation(2));
				CraftEntity craftEntity = ObjectUnwrapper.convert(entity);
				EntityMinecartAbstract ema = (EntityMinecartAbstract) craftEntity.getHandle();

				ema.k(parameters.getInt(0));
				ema.l(parameters.getInt(1));
				return entity.getEntityId();
			}
		});

		lib.set("spawnControlledEntity", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				IEntity entity = no.runsafe.framework.minecraft.entity.EntityType.getTypeByName(parameters.getString(0)).spawn(parameters.getLocation(1));
				Entity base = ObjectUnwrapper.getMinecraft(entity);

				if (base instanceof EntityInsentient)
				{
					EntityInsentient creature = (EntityCreature) base;
					try
					{
						Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
						gsa.setAccessible(true);

						Field gs = EntityInsentient.class.getDeclaredField("goalSelector");
						Field ts = EntityInsentient.class.getDeclaredField("targetSelector");

						gs.setAccessible(true);
						ts.setAccessible(true);

						PathfinderGoalSelector goalSelector = (PathfinderGoalSelector) gs.get(creature);
						PathfinderGoalSelector targetSelector = (PathfinderGoalSelector) ts.get(creature);

						gsa.set(goalSelector, new UnsafeList());
						gsa.set(targetSelector, new UnsafeList());

						goalSelector.a(1, new PathfinderGoToTarget(creature));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				return entity.getEntityId();
			}
		});

		lib.set("goTo", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				ILocation targetLocation = parameters.getLocation(1);
				IEntity entity = targetLocation.getWorld().getEntityById(parameters.getInt(0));

				if (entity == null)
					return;

				EntityDriver.addTarget(entity, targetLocation, parameters.getFloat(5));
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
