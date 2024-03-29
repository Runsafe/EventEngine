package no.runsafe.eventengine.handlers;

import no.runsafe.framework.api.entity.ILivingEntity;
import no.runsafe.framework.api.event.vehicle.IVehicleExit;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.vehicle.RunsafeVehicleExitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class SeatbeltHandler implements IVehicleExit
{
	@Override
	public void OnVehicleExit(final RunsafeVehicleExitEvent event)
	{
		final ILivingEntity rider = event.getExiter();
		if (players.contains(rider.getUniqueId()))
			event.cancel();
	}

	public static void lockPlayer(IPlayer player)
	{
		players.add(player.getUniqueId());
	}

	public static void unlockPlayer(IPlayer player)
	{
		players.remove(player.getUniqueId());
	}

	private static final List<UUID> players = new ArrayList<>(0);
}
