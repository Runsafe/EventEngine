package no.runsafe.eventengine.functions;

import no.runsafe.eventengine.Plugin;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class player extends TwoArgFunction
{
	public player()
	{
		Plugin.console.write("PLAYer OBJECT HAS BEEN CONSTRUCTED. RUN FOR THE HILLS");
	}

	@Override
	public LuaValue call(LuaValue modName, LuaValue env)
	{
		Plugin.console.write("PLAYER OBJECT HAS BEEN CALLED");
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
