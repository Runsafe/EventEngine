package no.runsafe.eventengine.engine.hooks;

import com.google.common.base.Strings;
import no.runsafe.eventengine.EventEngine;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.ITimer;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.CancellableEvent;
import no.runsafe.framework.api.event.IAsyncEvent;
import no.runsafe.framework.api.event.block.IBlockBreak;
import no.runsafe.framework.api.event.block.IBlockRedstone;
import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.extension.block.RunsafeBlock;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockRedstoneEvent;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.event.player.*;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookHandler
	implements IPlayerChatEvent, IPlayerCustomEvent, IPlayerJoinEvent, IPlayerQuitEvent, IPlayerInteractEvent,
	           IBlockRedstone, IBlockBreak, IPlayerLeftClickBlockEvent, IPlayerDamageEvent, IPlayerDeathEvent,
	           IPlayerDropItemEvent, IPlayerPickupItemEvent, IAsyncEvent
{
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
			hooks.put(type, new ArrayList<>());
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
		List<Hook> hooks = getHooks(HookType.CHAT_MESSAGE);
		if (hooks == null || hooks.isEmpty()) return;

		IPlayer player = event.getPlayer();
		IWorld playerWorld = player.getWorld();

		if (playerWorld == null) return;

		String message = event.getMessage();
		// ignore empty messages
		if (Strings.isNullOrEmpty(message)) return;

		for (Hook hook : hooks)
		{
			if (!hook.getWorld().isWorld(player.getWorld())) continue;

			LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(player.getName()));
			table.set("message", LuaValue.valueOf(message));
			ITimer timer = execute(hook, table);
			event.addCancellationHandle(timer::cancel);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		HookType type = null;
		String eventType = event.getEvent();

		switch (eventType)
		{
			case "region.enter":
				type = HookType.REGION_ENTER;
				break;
			case "region.leave":
				type = HookType.REGION_LEAVE;
				break;
		}

		if (type == null) return;

		List<Hook> hooks = getHooks(type);
		if (hooks == null || hooks.isEmpty()) return;

		for (final Hook hook : hooks)
		{
			Map<String, String> data = (Map<String, String>) event.getData();
			if (!((String) hook.getData()).equalsIgnoreCase(String.format("%s-%s", data.get("world"), data.get("region"))))
				continue;

			final LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(event.getPlayer().getName()));
			execute(hook, table);
		}
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_DEATH);
		if (hooks == null || hooks.isEmpty()) return;

		IPlayer player = event.getEntity();
		for (Hook hook : hooks)
		{
			IWorld hookWorld = hook.getWorld();
			if (!hookWorld.isWorld(player.getWorld())) continue;

			LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(player.getName()));
			execute(hook, table);
		}
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
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			if (hook.getPlayerName().equalsIgnoreCase(player.getName()))
				execute(hook);
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		EventEngine.Debugger.debugFiner("Checking interact event on thread #%d %s", Thread.currentThread().getId(),
		                 Thread.currentThread().getName()
		);
		EventEngine.Debugger.debugFine("Interact event detected");
		List<Hook> hooks = getHooks(HookType.INTERACT);

		if (hooks == null || hooks.isEmpty()) return;

		EventEngine.Debugger.debugFine("Hooks not null");
		for (Hook hook : hooks)
		{
			EventEngine.Debugger.debugFine("Processing hook...");
			IBlock block = event.getBlock();
			if (hook.getData() != null)
				if (block == null || block.getMaterial().getItemID() != (Integer) hook.getData()) continue;

			EventEngine.Debugger.debugFine("Block is not null");

			IWorld hookWorld = hook.getWorld();
			ILocation location = block.getLocation();
			if (hookWorld == null)
			{
				EventEngine.Debugger.debugFine("Hook world is null, using location");
				if (!location.getWorld().getName().equals(hook.getLocation().getWorld().getName()))
				{
					return;
				}
				EventEngine.Debugger.debugFine("Correct world!");
				if (!(location.distance(hook.getLocation()) < 1)) return;
				EventEngine.Debugger.debugFine("Distance is less than 1");
				LuaTable table = new LuaTable();
				if (event.getPlayer() != null) table.set("player", LuaValue.valueOf(event.getPlayer().getName()));

				table.set("x", LuaValue.valueOf(location.getBlockX()));
				table.set("y", LuaValue.valueOf(location.getBlockY()));
				table.set("z", LuaValue.valueOf(location.getBlockZ()));
				table.set("blockID", LuaValue.valueOf(block.getMaterial().getItemID()));
				table.set("blockData", LuaValue.valueOf((block).getData()));

				execute(hook, table);
				return;
			}
			if (!hookWorld.getName().equals(block.getWorld().getName())) continue;

			EventEngine.Debugger.debugFine("Hook world is not null, sending location data");
			LuaTable table = new LuaTable();
			if (event.getPlayer() != null) table.set("player", LuaValue.valueOf(event.getPlayer().getName()));

			table.set("x", LuaValue.valueOf(location.getBlockX()));
			table.set("y", LuaValue.valueOf(location.getBlockY()));
			table.set("z", LuaValue.valueOf(location.getBlockZ()));
			table.set("blockID", LuaValue.valueOf(block.getMaterial().getItemID()));
			table.set("blockData", LuaValue.valueOf((block).getData()));

			ITimer timer = execute(hook, table);
			event.addCancellationHandle(timer::cancel);
		}
	}

	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		if (event.getNewCurrent() <= 0 || event.getOldCurrent() != 0) return;

		List<Hook> hooks = getHooks(HookType.BLOCK_GAINS_CURRENT);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
		{
			IBlock block = event.getBlock();
			if (block == null) continue;
			ILocation location = block.getLocation();
			if (location.getWorld().getName().equals(hook.getLocation().getWorld().getName()))
				if (location.distance(hook.getLocation()) < 1) execute(hook);
		}
	}

	@Override
	public boolean OnBlockBreak(IPlayer player, IBlock block)
	{
		List<Hook> hooks = getHooks(HookType.BLOCK_BREAK);
		if (hooks == null || hooks.isEmpty()) return true;

		ILocation blockLocation = block.getLocation();
		String blockWorld = blockLocation.getWorld().getName();
		for (Hook hook : hooks)
		{
			IWorld world = hook.getWorld();
			if (world != null && !blockWorld.equals(world.getName())) return true;

			LuaTable table = new LuaTable();
			if (player != null) table.set("player", LuaValue.valueOf(player.getName()));

			table.set("world", LuaValue.valueOf(blockWorld));
			table.set("x", LuaValue.valueOf(blockLocation.getBlockX()));
			table.set("y", LuaValue.valueOf(blockLocation.getBlockY()));
			table.set("z", LuaValue.valueOf(blockLocation.getBlockZ()));
			table.set("blockID", LuaValue.valueOf(block.getMaterial().getItemID()));
			table.set("blockData", LuaValue.valueOf(((RunsafeBlock) block).getData()));

			execute(hook, table);
		}
		return true;
	}

	@Override
	public void OnPlayerLeftClick(RunsafePlayerClickEvent event)
	{
		List<Hook> hooks = getHooks(HookType.LEFT_CLICK_BLOCK);
		if (hooks == null || hooks.isEmpty()) return;

		IBlock block = event.getBlock();
		Item material = block.getMaterial();
		ILocation blockLocation = block.getLocation();
		String blockWorldName = blockLocation.getWorld().getName();
		String playerName = event.getPlayer().getName();
		for (Hook hook : hooks)
		{
			IWorld world = hook.getWorld();
			if (world != null && !blockWorldName.equals(world.getName())) return;

			LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(playerName));
			table.set("world", LuaValue.valueOf(blockWorldName));
			table.set("x", LuaValue.valueOf(blockLocation.getBlockX()));
			table.set("y", LuaValue.valueOf(blockLocation.getBlockY()));
			table.set("z", LuaValue.valueOf(blockLocation.getBlockZ()));
			table.set("blockID", LuaValue.valueOf(material.getItemID()));
			table.set("blockData", LuaValue.valueOf(material.getData()));

			ITimer timer = execute(hook, table);
			event.addCancellationHandle(timer::cancel);
		}
	}

	@Override
	public void OnPlayerDamage(IPlayer player, RunsafeEntityDamageEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_DAMAGE);
		if (hooks == null || hooks.isEmpty()) return;

		IWorld damageWorld = player.getWorld();

		LuaString playerName = LuaValue.valueOf(player.getName());
		LuaString damageCause = LuaValue.valueOf(event.getCause().name());
		LuaValue damage = LuaValue.valueOf(event.getDamage());

		for (Hook hook : hooks)
		{
			IWorld world = hook.getWorld();
			if (world == null || !world.isWorld(damageWorld)) return;

			LuaTable table = new LuaTable();
			table.set("player", playerName);
			table.set("playerHealth", player.getHealth());
			table.set("playerMaxHealth", player.getMaxHealth());
			table.set("damage", damage);
			table.set("cause", damageCause);

			ITimer timer = execute(hook, table);
			event.addCancellationHandle(timer::cancel);
		}
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_ITEM_DROP);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			handleItemHook(hook, event.getPlayer(), event.getItem().getItemStack(), event);
	}

	@Override
	public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event)
	{
		List<Hook> hooks = getHooks(HookType.PLAYER_ITEM_PICKUP);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			handleItemHook(hook, event.getPlayer(), event.getItem().getItemStack(), event);
	}

	private void handleItemHook(Hook hook, IPlayer player, RunsafeMeta item, CancellableEvent event)
	{
		IWorld hookWorld = hook.getWorld();
		if (!hookWorld.isWorld(player.getWorld())) return;
		LuaTable table = new LuaTable();
		table.set("player", player.getName());
		table.set("itemID", item.getItemId());
		table.set("itemName", item.hasDisplayName() ? item.getDisplayName() : item.getNormalName());

		ITimer hookTrigger = execute(hook, table);
		event.addCancellationHandle(hookTrigger::cancel);
	}

	private ITimer execute(Hook hook, LuaTable arguments)
	{
		return scheduler.createSyncTimer(() -> hook.execute(arguments), 1L);
	}

	private ITimer execute(Hook hook)
	{
		return scheduler.createSyncTimer(hook::execute, 1L);
	}

	private final IScheduler scheduler;

	private static final HashMap<HookType, List<Hook>> hooks = new HashMap<>();
	private static String context;
}
