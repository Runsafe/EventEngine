package no.runsafe.eventengine.handlers;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.entity.ILivingEntity;
import no.runsafe.framework.api.event.vehicle.IVehicleExit;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.vehicle.RunsafeVehicleExitEvent;

import java.util.ArrayList;
import java.util.List;

public class SeatbeltHandler implements IVehicleExit
{
	public SeatbeltHandler(IScheduler scheduler, IConsole console)
	{
		this.scheduler = scheduler;
		this.console = console;
	}

	@Override
	public void OnVehicleExit(final RunsafeVehicleExitEvent event)
	{
		final ILivingEntity rider = event.getExiter();
		console.logInformation("Vehicle exit event!");
		if (rider instanceof IPlayer)
		{
			console.logInformation("Rider is a player");
			String playerName = ((IPlayer) rider).getName();
			if (players.contains(playerName))
			{
				console.logInformation("Player is locked.");
				scheduler.startSyncTask(new Runnable()
				{
					@Override
					public void run()
					{
						event.getVehicle().setPassenger(rider);
					}
				}, 1L);
			}
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
	private final IScheduler scheduler;
	private final IConsole console;
}
