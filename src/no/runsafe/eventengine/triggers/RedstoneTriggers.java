package no.runsafe.eventengine.triggers;

import no.runsafe.framework.event.block.IBlockBreakEvent;
import no.runsafe.framework.event.block.IBlockPlace;
import no.runsafe.framework.event.block.IBlockRedstone;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.event.block.RunsafeBlockBreakEvent;
import no.runsafe.framework.server.event.block.RunsafeBlockRedstoneEvent;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.item.meta.RunsafeItemMeta;
import no.runsafe.framework.server.player.RunsafePlayer;

public class RedstoneTriggers implements IBlockRedstone, IBlockBreakEvent, IBlockPlace
{
	public RedstoneTriggers(TriggerHandler triggerHandler)
	{
		this.triggerHandler = triggerHandler;
	}

	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		RunsafeBlock block = event.getBlock();
		if (block.is(Item.Unavailable.CommandBlock) && event.getOldCurrent() == 0 && event.getNewCurrent() > 0)
			this.triggerHandler.trigger(block.getLocation());
	}

	@Override
	public void OnBlockBreakEvent(RunsafeBlockBreakEvent event)
	{
		RunsafeBlock block = event.getBlock();
		if (block.is(Item.Unavailable.CommandBlock))
			this.triggerHandler.deleteTriggersAtLocation(block.getLocation());
	}

	@Override
	public boolean OnBlockPlace(RunsafePlayer player, RunsafeBlock block)
	{
		RunsafeItemStack item = player.getItemInHand();
		if (item != null && item.is(Item.Unavailable.CommandBlock))
		{
			if (player.hasPermission("runsafe.eventengine.triggers"))
			{
				RunsafeItemMeta meta = item.getItemMeta();
				String displayName = meta.getDisplayName();

				if (displayName != null)
				{
					triggerHandler.addTrigger(block.getLocation(), displayName);
					player.sendColouredMessage("&2Script trigger created.");
				}
			}
		}
		return true;
	}

	private TriggerHandler triggerHandler;
}
