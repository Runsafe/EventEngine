package no.runsafe.eventengine.functions;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class boobs extends TwoArgFunction
{
	public boobs() {}

	@Override
	public LuaValue call(LuaValue modName, LuaValue env)
	{
		LuaTable player = tableOf();
		player.set("test", new test());

		env.set("player", player);
		//env.checkglobals().package_.loaded.set("player", player);
		return player;
	}

	static class test extends ZeroArgFunction
	{
		@Override
		public LuaValue call()
		{
			return valueOf("Test");
		}
	}
}
