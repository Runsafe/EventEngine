package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeFireworkEffect;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.entity.PassiveEntity;
import no.runsafe.framework.server.entity.ProjectileEntity;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.item.meta.RunsafeFireworkMeta;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;

public class EffectLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("strikeLightning", new LightningStrike());
		lib.set("explosion", new Explosion());
		lib.set("firework", new SpawnFirework());

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

	static class SpawnFirework extends VarArgFunction
	{
		// worldName, x, y, z, type, colour1, colour2, flicker, trail, power
		public Varargs invoke(Varargs args)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(args.checkstring(1));
			if (world == null) return null;

			RunsafeLocation location = new RunsafeLocation(world, args.checkdouble(2), args.checkdouble(3), args.checkdouble(4));
			RunsafeEntity entity = world.spawnCreature(location, ProjectileEntity.Firework.getId());
			Firework firework = (Firework) entity.getRaw();
			FireworkMeta meta = firework.getFireworkMeta();

			FireworkEffect effect = FireworkEffect
					.builder()
					.withColor(EffectLibrary.colourID.get(args.checkint(5)))
					.withFade(EffectLibrary.colourID.get(args.checkint(6)))
					.flicker(args.checkboolean(7))
					.trail(args.checkboolean(8))
					.build();

			meta.addEffect(effect);
			meta.setPower(args.checkint(9));
			firework.setFireworkMeta(meta);
			return null;
		}
	}

	public static HashMap<Integer, Color> colourID = new HashMap<Integer, Color>();

	static
	{
		EffectLibrary.colourID.put(1, Color.AQUA);
		EffectLibrary.colourID.put(2, Color.BLACK);
		EffectLibrary.colourID.put(3, Color.BLUE);
		EffectLibrary.colourID.put(4, Color.FUCHSIA);
		EffectLibrary.colourID.put(5, Color.GRAY);
		EffectLibrary.colourID.put(6, Color.GREEN);
		EffectLibrary.colourID.put(7, Color.LIME);
		EffectLibrary.colourID.put(8, Color.MAROON);
		EffectLibrary.colourID.put(9, Color.NAVY);
		EffectLibrary.colourID.put(10, Color.OLIVE);
		EffectLibrary.colourID.put(11, Color.ORANGE);
		EffectLibrary.colourID.put(12, Color.PURPLE);
		EffectLibrary.colourID.put(13, Color.RED);
		EffectLibrary.colourID.put(14, Color.SILVER);
		EffectLibrary.colourID.put(15, Color.TEAL);
		EffectLibrary.colourID.put(16, Color.WHITE);
		EffectLibrary.colourID.put(17, Color.YELLOW);
	}
}
