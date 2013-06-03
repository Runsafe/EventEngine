package no.runsafe.eventengine.functions;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class KillPlayer extends OneArgFunction
{
	public KillPlayer() {}

	@Override
	public LuaValue call(LuaValue playerString)
	{
		RunsafePlayer player = RunsafeServer.Instance.getPlayerExact(playerString.toString());
		player.setHealth(0);

		return null;
	}
}
