package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
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
		public Varargs invoke(Varargs args)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(args.checkstring(1));
			if (world != null)
				world.strikeLightningEffect(new RunsafeLocation(world, args.checkdouble(2), args.checkdouble(3), args.checkdouble(4)));

			return null;
		}
	}

	static class Explosion extends VarArgFunction
	{
		public Varargs invoke(Varargs args)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(args.checkstring(1));
			if (world != null)
			{
				RunsafeLocation location = new RunsafeLocation(world, args.checkdouble(2), args.checkdouble(3), args.checkdouble(4));
				world.createExplosion(location, args.checkint(5), args.checkboolean(6), args.checkboolean(7));
			}
			return null;
		}
	}
}
