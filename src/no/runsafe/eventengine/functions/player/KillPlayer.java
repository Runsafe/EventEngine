package no.runsafe.eventengine.functions.player;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class KillPlayer extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue playerName)
	{
		RunsafePlayer player = RunsafeServer.Instance.getPlayerExact(playerName.toString());
		player.setHealth(0);
		return null;
	}
}
