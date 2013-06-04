package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.luaj.vm2.LuaValue;

public class ObjectLibrary
{
	public static RunsafePlayer getPlayer(String playerName)
	{
		return RunsafeServer.Instance.getPlayerExact(playerName);
	}

	public static RunsafePlayer getPlayer(LuaValue playerName)
	{
		return ObjectLibrary.getPlayer(playerName.toString());
	}

	public static RunsafeWorld getWorld(LuaValue worldName)
	{
		return RunsafeServer.Instance.getWorld(worldName.toString());
	}

	public static boolean canEditPlayer(RunsafePlayer player)
	{
		if (player != null)
			if (player.isOnline())
				return true;

		return false;
	}
}
