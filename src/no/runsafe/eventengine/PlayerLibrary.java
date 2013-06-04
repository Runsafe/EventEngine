package no.runsafe.eventengine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class PlayerLibrary extends OneArgFunction
{
	Globals globals;
	@Override
	public LuaValue call(LuaValue env)
	{
		Plugin.console.write("PLAYER LIBRARY CALLED");
		globals = env.checkglobals();
		LuaTable lib = new LuaTable();

		lib.set("test", new Test());

		env.get("engine").set("player", lib);
		return lib;
	}

	static class Test extends ZeroArgFunction
	{
		@Override
		public LuaValue call()
		{
			return valueOf("Test");
		}
	}
}
