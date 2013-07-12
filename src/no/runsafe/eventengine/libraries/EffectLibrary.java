package no.runsafe.eventengine.libraries;

import no.runsafe.framework.lua.FunctionParameters;
import no.runsafe.framework.lua.RunsafeLuaFunction;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.HashMap;
import java.util.List;

public class EffectLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("strikeLightning", new LightningStrike());
		lib.set("explosion", new Explosion());
		lib.set("firework", new SpawnFirework());

		env.get("api").set("effects", lib);
		return lib;
	}

	static class LightningStrike extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafeLocation location = parameters.getLocation(0);
			location.getWorld().strikeLightningEffect(location);
			return null;
		}
	}

	static class Explosion extends RunsafeLuaFunction
	{
		// world, x, y, z, power, break, fire
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafeLocation location = parameters.getLocation(0);
			location.getWorld().createExplosion(location, parameters.getInt(4), parameters.getBool(5), parameters.getBool(6));
			return null;
		}
	}

	static class SpawnFirework extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			RunsafeLocation location = parameters.getLocation(0);
			RunsafeEntity entity = location.getWorld().spawnCreature(location, ProjectileEntity.Firework.getId());

			Firework firework = (Firework) entity.getRaw();
			FireworkMeta meta = firework.getFireworkMeta();

			FireworkEffect effect = FireworkEffect
					.builder()
					.with(FireworkEffect.Type.valueOf(parameters.getString(4)))
					.withColor(EffectLibrary.colourID.get(parameters.getInt(5)))
					.withFade(EffectLibrary.colourID.get(parameters.getInt(6)))
					.flicker(parameters.getBool(7))
					.trail(parameters.getBool(8))
					.build();

			meta.addEffect(effect);
			meta.setPower(parameters.getInt(9));
			firework.setFireworkMeta(meta);
			return null;
		}
	}

	public static final HashMap<Integer, Color> colourID = new HashMap<Integer, Color>();

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
