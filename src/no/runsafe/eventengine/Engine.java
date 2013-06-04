package no.runsafe.eventengine;

import no.runsafe.eventengine.libraries.EffectLibrary;
import no.runsafe.eventengine.libraries.PlayerLibrary;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class Engine extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		Environment.global = env.checkglobals();
		LuaTable lib = new LuaTable();

		env.set("engine", lib);
		env.get("package").get("loaded").set("engine", lib);

		Environment.global.load(new PlayerLibrary());
		Environment.global.load(new EffectLibrary());
		return lib;
	}
}
