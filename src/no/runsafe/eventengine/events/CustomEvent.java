package no.runsafe.eventengine.events;

import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class CustomEvent extends RunsafeCustomEvent
{
	public CustomEvent(RunsafePlayer player, String event)
	{
		super(player, event);
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
