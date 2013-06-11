package no.runsafe.eventengine.objects;

import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.luaj.vm2.LuaValue;

public class LuaPlayer
{
	public LuaPlayer(LuaValue playerName)
	{
		this.player = RunsafeServer.Instance.getPlayerExact(playerName.toString());
	}

	public boolean isPlayer()
	{
		return this.player != null;
	}

	public RunsafePlayer player()
	{
		return this.player;
	}

	private final RunsafePlayer player;
}
