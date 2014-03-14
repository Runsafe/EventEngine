package no.runsafe.eventengine.handlers;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.entity.ILivingEntity;
import no.runsafe.framework.api.event.vehicle.IVehicleExit;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.vehicle.RunsafeVehicleExitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeatbeltHandler implements IVehicleExit
{
	public SeatbeltHandler(IScheduler scheduler, IConsole consoleInstance)
	{
		this.scheduler = scheduler;
		console = consoleInstance;
	}

	@Override
	public void OnVehicleExit(final RunsafeVehicleExitEvent event)
	{
		console.logInformation("Vehicle exit event detected");
		final ILivingEntity rider = event.getExiter();
		if (players.contains(rider.getUniqueId()))
		{
			console.logInformation("Player ID is in the lock list");
			scheduler.startSyncTask(new Runnable()
			{
				@Override
				public void run()
				{
					console.logInformation("Remounting");
					event.getVehicle().setPassenger(rider);
				}
			}, 1L);
		}
	}

	public static void lockPlayer(IPlayer player)
	{
		console.logInformation("Locking player!");
		players.add(player.getUniqueId());
	}

	public static void unlockPlayer(IPlayer player)
	{
		console.logInformation("Unlocking player..");
		players.remove(player.getUniqueId());
	}

	private static final List<UUID> players = new ArrayList<UUID>(0);
	private final IScheduler scheduler;
	private static IConsole console;
}
