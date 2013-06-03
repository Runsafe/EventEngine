package no.runsafe.eventengine.functions;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class Player extends OneArgFunction
{
	public Player() {}

	@Override
	public LuaValue call(LuaValue env)
	{
		Globals globals = env.checkglobals();
		LuaTable player = new LuaTable();
		player.set("kill", new kill());
		player.set("test", new test());

		env.set("player", player);
		globals.package_.loaded.set("player", player);
		return player;
	}

	static class test extends ZeroArgFunction
	{
		@Override
		public LuaValue call()
		{
			RunsafeServer.Instance.broadcastMessage("Test");
			return valueOf("Test");
		}
	}

	static class kill extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName)
		{
			RunsafePlayer player = RunsafeServer.Instance.getPlayerExact(playerName.toString());
			player.setHealth(0);
			return null;
		}
	}
}
