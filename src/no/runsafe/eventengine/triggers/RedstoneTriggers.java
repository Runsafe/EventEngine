package no.runsafe.eventengine.triggers;

import no.runsafe.framework.event.block.IBlockRedstone;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.event.block.RunsafeBlockRedstoneEvent;

public class RedstoneTriggers implements IBlockRedstone
{
	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		RunsafeBlock block = event.getBlock();
		if (block.is(Item.BuildingBlock.Sponge))
		{
			RunsafeServer.Instance.broadcastMessage("Current: " + event.getNewCurrent());
		}
	}
}
