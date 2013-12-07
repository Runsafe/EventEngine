package no.runsafe.eventengine.events;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class CustomEvent extends RunsafeCustomEvent
{
	public CustomEvent(IPlayer player, String event)
	{
		super(player, event);
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
