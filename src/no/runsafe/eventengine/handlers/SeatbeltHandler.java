package no.runsafe.eventengine.handlers;

import no.runsafe.framework.api.entity.ILivingEntity;
import no.runsafe.framework.api.event.vehicle.IVehicleExit;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.vehicle.RunsafeVehicleExitEvent;

import java.util.ArrayList;
import java.util.List;

public class SeatbeltHandler implements IVehicleExit
{
	@Override
	public void OnVehicleExit(RunsafeVehicleExitEvent event)
	{
		ILivingEntity rider = event.getExiter();
		if (rider instanceof IPlayer)
		{
			String playerName = ((IPlayer) rider).getName();
			if (players.contains(playerName))
				event.cancel();
		}
	}

	public static void lockPlayer(IPlayer player)
	{
		players.add(player.getName());
	}

	public static void unlockPlayer(IPlayer player)
	{
		players.remove(player.getName());
	}

	private static final List<String> players = new ArrayList<String>(0);
}
