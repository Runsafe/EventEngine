package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import no.runsafe.framework.minecraft.WorldEffect;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.luaj.vm2.LuaTable;

import java.util.HashMap;

public class EffectLibrary extends Library
{
	public EffectLibrary(RunsafePlugin plugin)
	{
		super(plugin, "effects");
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("strikeLightning", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				LightningStrike(parameters.getLocation(0));
			}
		});

		lib.set("playEffect", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				parameters.getLocation(0).playEffect(
					WorldEffect.valueOf(parameters.getString(4)),
					parameters.getFloat(5),
					parameters.getInt(6),
					parameters.getDouble(7)
				);
			}
		});

		lib.set("explosion", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				Explosion(parameters.getLocation(0), parameters.getInt(4), parameters.getBool(5), parameters.getBool(6));
			}
		});
		lib.set("firework", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				SpawnFirework(
					parameters.getLocation(0),
					FireworkEffect.Type.valueOf(parameters.getString(4)),
					EffectLibrary.colourID.get(parameters.getInt(5)),
					EffectLibrary.colourID.get(parameters.getInt(6)),
					parameters.getBool(7),
					parameters.getBool(8),
					parameters.getInt(9)
				);
			}
		});
		return lib;
	}

	private static void LightningStrike(ILocation location)
	{
		location.getWorld().strikeLightningEffect(location);
	}

	private static void Explosion(ILocation location, int power, boolean setFire, boolean breakBlocks)
	{
		location.getWorld().createExplosion(location, power, setFire, breakBlocks);
	}

	private void SpawnFirework(
		ILocation location, FireworkEffect.Type type, Color color, Color fade, boolean flicker, boolean trail, int power
	)
	{
		IEntity entity = location.getWorld().spawnCreature(location, ProjectileEntity.Firework.getName());

		Firework firework = (Firework) ((RunsafeEntity) entity).getRaw();
		FireworkMeta meta = firework.getFireworkMeta();

		FireworkEffect effect = FireworkEffect.builder()
			.with(type).withColor(color).withFade(fade).flicker(flicker).trail(trail)
			.build();

		meta.addEffect(effect);
		meta.setPower(power);
		firework.setFireworkMeta(meta);
	}

	private static final HashMap<Integer, Color> colourID = new HashMap<>();

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
