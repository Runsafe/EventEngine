package no.runsafe.eventengine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class Engine extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		Globals globals = env.checkglobals();
		LuaTable lib = new LuaTable();

		env.set("engine", lib);
		env.get("package").get("loaded").set("engine", lib);

		globals.load(new PlayerLibrary());

		return lib;
	}
}
