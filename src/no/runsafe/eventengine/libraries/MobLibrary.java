package no.runsafe.eventengine.libraries;

import net.minecraft.server.v1_7_R1.EntityMinecartAbstract;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.lua.*;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.minecraft.entity.PassiveEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
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
		return lib;
	}
}
