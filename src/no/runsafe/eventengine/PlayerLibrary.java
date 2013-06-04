package no.runsafe.eventengine;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class PlayerLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("kill", new Kill());
		lib.set("sendMessage", new SendMessage());

		env.get("engine").set("player", lib);
		return lib;
	}

	private static RunsafePlayer getPlayer(String playerName)
	{
		return RunsafeServer.Instance.getPlayerExact(playerName);
	}

	private static RunsafePlayer getPlayer(LuaValue playerName)
	{
		return PlayerLibrary.getPlayer(playerName.toString());
	}

	static class Kill extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName)
		{
			RunsafePlayer player = PlayerLibrary.getPlayer(playerName);
			if (player != null)
				if (player.isOnline())
					player.setHealth(0);

			return null;
		}
	}

	static class SendMessage extends TwoArgFunction
	{
		@Override
		public LuaValue call(LuaValue playerName, LuaValue message)
		{
			RunsafePlayer player = PlayerLibrary.getPlayer(playerName);
			if (player != null)
				if (player.isOnline())
					player.sendColouredMessage(message.toString());

			return null;
		}
	}
}
