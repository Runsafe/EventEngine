package no.runsafe.eventengine.objects;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaValue;

public class LuaPlayer extends RunsafePlayer
{
	public LuaPlayer(LuaValue playerName)
	{
		super(RunsafeServer.Instance.getPlayerExact(playerName.toString()).getRawPlayer());
	}
}
