package no.runsafe.eventengine.functions.player;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class Player extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable player = new LuaTable();
		player.set("kill", new KillPlayer());
		env.set("player", player);
		env.get("package").get("loaded").set("player", player);
		return player;
	}
}
