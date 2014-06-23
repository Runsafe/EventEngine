package no.runsafe.eventengine.libraries;

import net.minecraft.server.v1_7_R3.*;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.minecraft.entity.PassiveEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeItemFrame;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
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
				return no.runsafe.framework.minecraft.entity.EntityType.getTypeByName(parameters.getString(0)).spawn(parameters.getLocation(1)).getEntityId();
			}
		});

		lib.set("getEntity", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				ILocation location =  parameters.getLocation(0);
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
					itemFrame.setItem(no.runsafe.framework.minecraft.Item.get(parameters.getString(2)).getItem());
				}
			}
		});

		lib.set("addEffect", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				IEntity entity = parameters.getWorld(0).getEntityById(parameters.getInt(1));
				if (entity != null)
				{
					EntityLiving living = (EntityLiving) ObjectUnwrapper.getMinecraft(entity);
					living.addEffect(new MobEffect(
							MobEffectList.byId[parameters.getInt(2)].id,
							parameters.getInt(3),
							parameters.getInt(4),
							parameters.getBool(5)
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
