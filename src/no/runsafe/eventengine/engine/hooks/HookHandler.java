package no.runsafe.eventengine.engine.hooks;

import no.runsafe.framework.api.event.block.IBlockRedstone;
import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockRedstoneEvent;
import no.runsafe.framework.minecraft.event.player.*;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookHandler implements IPlayerChatEvent, IPlayerCustomEvent, IPlayerJoinEvent, IPlayerQuitEvent, IPlayerInteractEvent, IBlockRedstone
{
	public static void registerHook(Hook hook)
	{
		HookType type = hook.getType();
		if (!HookHandler.hooks.containsKey(type))
			HookHandler.hooks.put(type, new ArrayList<Hook>());

		HookHandler.hooks.get(type).add(hook);
	}

	public static List<Hook> getHooks(HookType type)
	{
		if (HookHandler.hooks.containsKey(type))
			return HookHandler.hooks.get(type);

		return null;
	}

	public static void clearHooks()
	{
		HookHandler.hooks.clear();
	}

	@Override
	public void OnPlayerChatEvent(RunsafePlayerChatEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.CHAT_MESSAGE);

		if (hooks != null)
			for (Hook hook : hooks)
				if (hook.getPlayerName().equalsIgnoreCase(event.getPlayer().getName()) &&
						event.getMessage().equalsIgnoreCase((String) hook.getData()))
					hook.execute();
	}

	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		HookType type = null;
		String eventType = event.getEvent();

		if (eventType.equals("region.enter"))
			type = HookType.REGION_ENTER;
		else if (eventType.equals("region.leave"))
			type = HookType.REGION_LEAVE;

		if (type != null)
		{
			List<Hook> hooks = HookHandler.getHooks(type);

			if (hooks != null)
			{
				for (Hook hook : hooks)
				{
					if (hook.getPlayerName().equalsIgnoreCase(event.getPlayer().getName()))
					{
						Map<String, String> data = (Map<String, String>) event.getData();
						if (((String) hook.getData()).equalsIgnoreCase(String.format("%s-%s", data.get("world"), data.get("region"))))
							hook.execute();
					}
				}
			}
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		this.playerLogEvent(event.getPlayer(), HookType.PLAYER_LOGIN);
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		this.playerLogEvent(event.getPlayer(), HookType.PLAYER_LOGOUT);
	}

	private void playerLogEvent(RunsafePlayer player, HookType type)
	{
		List<Hook> hooks = HookHandler.getHooks(type);

		if (hooks != null)
			for (Hook hook : hooks)
				if (hook.getPlayerName().equalsIgnoreCase(player.getName()))
					hook.execute();
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.INTERACT);

		if (hooks != null)
		{
			for (Hook hook : hooks)
			{
				RunsafeBlock block = event.getBlock();
				if (block != null && block.getTypeId() == (Integer) hook.getData())
				{
					RunsafeLocation location = block.getLocation();
					if (location.distance(hook.getLocation()) < 1)
						hook.execute();
				}
			}
		}
	}

	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		if (event.getNewCurrent() > 0 && event.getOldCurrent() == 0)
		{
			List<Hook> hooks = HookHandler.getHooks(HookType.BLOCK_GAINS_CURRENT);

			if (hooks != null)
			{
				for (Hook hook : hooks)
				{
					RunsafeBlock block = event.getBlock();
					if (block != null)
					{
						RunsafeLocation location = block.getLocation();
						if (location.distance(hook.getLocation()) < 1)
							hook.execute();
					}
				}
			}
		}
	}

	private static HashMap<HookType, List<Hook>> hooks = new HashMap<HookType, List<Hook>>();
}
