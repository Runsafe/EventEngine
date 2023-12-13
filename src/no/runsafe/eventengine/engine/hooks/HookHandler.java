package no.runsafe.eventengine.engine.hooks;

import com.google.common.base.Strings;
import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.events.*;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.block.IBlockBreak;
import no.runsafe.framework.api.event.block.IBlockRedstone;
import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockRedstoneEvent;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.event.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handlers in this class should do a fast return when possible.
 * Keep in mind that some events happen A LOT, so try to keep the logic as slim as possible here
 */
public final class HookHandler
	implements IPlayerChatEvent, IPlayerCustomEvent, IPlayerJoinEvent, IPlayerQuitEvent, IPlayerInteractEvent,
	           IBlockRedstone, IBlockBreak, IPlayerLeftClickBlockEvent, IPlayerDamageEvent, IPlayerDeathEvent,
	           IPlayerDropItemEvent, IPlayerPickupItemEvent
{
	@SuppressWarnings("unused")
	public HookHandler(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public static void setContext(String context)
	{
		EventEngine.Debugger.debugInfo("context(%s)", context);
		HookHandler.context = context;
	}

	public static void registerHook(Hook hook)
	{
		hook.setContext(context);
		HookType type = hook.getType();
		if (!hooks.containsKey(type))
		{
			hooks.put(type, new ArrayList<>());
		}
		EventEngine.Debugger.debugInfo("Registering hook for event %s: %s", type, hook.getFunction());
		hooks.get(type).add(hook);
	}

	private static List<Hook> getHooks(HookType type)
	{
		return hooks.getOrDefault(type, null);
	}

	public static void clearHooks()
	{
		EventEngine.Debugger.debugInfo("Clearing all event hooks");
		hooks.clear();
	}

	@Override
	public void OnPlayerChatEvent(RunsafePlayerChatEvent event)
	{
		// ignore both empty messages and messages from the void
		IPlayer player = event.getPlayer();
		if (player == null || player.getWorld() == null || Strings.isNullOrEmpty(event.getMessage()))
		{
			return;
		}
		List<Hook> hooks = getHooks(HookType.CHAT_MESSAGE);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerChatEventHook(event, player, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		if (!(event.getData() instanceof Map))
		{
			return;
		}
		HookType type;
		String eventType = event.getEvent();
		switch (eventType)
		{
			case "region.enter":
				type = HookType.REGION_ENTER;
				break;
			case "region.leave":
				type = HookType.REGION_LEAVE;
				break;
			default:
				return;
		}
		List<Hook> hooks = getHooks(type);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		Map<?,?> data = (Map<?,?>)event.getData();
		String world = (String) data.get("world");
		String region = (String) data.get("region");
		new PlayerCustomEventHook(event, world, region, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_DEATH);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerDeathEventHook(event, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		playerLogEvent(event.getPlayer(), HookType.PLAYER_LOGIN);
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		playerLogEvent(event.getPlayer(), HookType.PLAYER_LOGOUT);
	}

	private void playerLogEvent(IPlayer player, HookType type)
	{
		List<Hook> hooks = getHooks(type);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerLogEventHook(player, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		EventEngine.Debugger.debugFiner(
			"Checking interact event on thread #%d %s",
			Thread.currentThread().getId(),
			Thread.currentThread().getName()
		);
		EventEngine.Debugger.debugFiner("Interact event detected");
		List<Hook> hooks = getHooks(HookType.INTERACT);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerInteractEventHook(event, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		if (event.getNewCurrent() <= 0 || event.getOldCurrent() != 0 || event.getBlock() == null)
		{
			return;
		}
		List<Hook> hooks = getHooks(HookType.BLOCK_GAINS_CURRENT);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new BlockRedstoneEventHook(event, scheduler).sendToBackground(hooks);
	}

	@Override
	public boolean OnBlockBreak(IPlayer player, IBlock block)
	{
		List<Hook> hooks = getHooks(HookType.BLOCK_BREAK);
		if (hooks == null || hooks.isEmpty())
		{
			return true;
		}
		new BlockBreakEventHook(player, block, scheduler).sendToBackground(hooks);
		return true;
	}

	@Override
	public void OnPlayerLeftClick(RunsafePlayerClickEvent event)
	{
		List<Hook> hooks = getHooks(HookType.LEFT_CLICK_BLOCK);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerLeftClickEventHook(event, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerDamage(IPlayer player, RunsafeEntityDamageEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_DAMAGE);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new PlayerDamageEventHook(event, player, scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_ITEM_DROP);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new ItemEventHook(event, event.getPlayer(), event.getItem().getItemStack(), scheduler).sendToBackground(hooks);
	}

	@Override
	public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_ITEM_PICKUP);
		if (hooks == null || hooks.isEmpty())
		{
			return;
		}
		new ItemEventHook(event, event.getPlayer(), event.getItem().getItemStack(), scheduler).sendToBackground(hooks);
	}

	private final IScheduler scheduler;

	private static final HashMap<HookType, List<Hook>> hooks = new HashMap<>();
	private static String context;
}