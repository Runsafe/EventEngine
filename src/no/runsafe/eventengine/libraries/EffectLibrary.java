package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class EffectLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("strikeLightning", new LightningStrike());
		lib.set("explosion", new Explosion());

		env.get("engine").set("effects", lib);
		return lib;
	}

	static class LightningStrike extends VarArgFunction
	{
		public LuaValue call(LuaValue worldName, LuaValue x, LuaValue y, LuaValue z)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(worldName);
			if (world != null)
				world.strikeLightningEffect(new RunsafeLocation(world, x.todouble(), y.todouble(), z.todouble()));

			return null;
		}
	}

	static class Explosion extends VarArgFunction
	{
		public LuaValue call(LuaValue worldName, LuaValue x, LuaValue y, LuaValue z, LuaValue power, LuaValue breakBlocks, LuaValue fire)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(worldName);
			if (world != null)
			{
				RunsafeLocation location = new RunsafeLocation(world, x.todouble(), y.todouble(), z.todouble());
				world.createExplosion(location, power.tofloat(), fire.toboolean(), breakBlocks.toboolean());
			}
			return null;
		}
	}
}
