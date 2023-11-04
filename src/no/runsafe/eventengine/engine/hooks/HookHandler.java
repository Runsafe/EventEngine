package no.runsafe.eventengine.engine.hooks;

import com.google.common.base.Strings;
import no.runsafe.eventengine.EventEngine;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.block.IBlockBreak;
import no.runsafe.framework.api.event.block.IBlockRedstone;
import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.api.log.IDebug;
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
	           IPlayerDropItemEvent, IPlayerPickupItemEvent
{
	public static void setContext(String context)
	{
		HookHandler.context = context;
	}

	public static void registerHook(Hook hook)
	{
		hook.setContext(context);
		HookType type = hook.getType();
		if (!HookHandler.hooks.containsKey(type)) HookHandler.hooks.put(type, new ArrayList<>());

		HookHandler.hooks.get(type).add(hook);
	}

	private static List<Hook> getHooks(HookType type)
	{
		if (HookHandler.hooks.containsKey(type)) return HookHandler.hooks.get(type);

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
			hook.execute(table);
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

		List<Hook> hooks = HookHandler.getHooks(type);
		if (hooks == null || hooks.isEmpty()) return;

		for (final Hook hook : hooks)
		{
			Map<String, String> data = (Map<String, String>) event.getData();
			if (!((String) hook.getData()).equalsIgnoreCase(String.format("%s-%s", data.get("world"), data.get("region"))))
				continue;

			final LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(event.getPlayer().getName()));
			hook.execute(table);
		}
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.PLAYER_DEATH);
		if (hooks == null || hooks.isEmpty()) return;

		IPlayer player = event.getEntity();
		for (Hook hook : hooks)
		{
			IWorld hookWorld = hook.getWorld();
			if (!hookWorld.isWorld(player.getWorld())) continue;

			LuaTable table = new LuaTable();
			table.set("player", LuaValue.valueOf(player.getName()));
			hook.execute(table);
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

	private void playerLogEvent(IPlayer player, HookType type)
	{
		List<Hook> hooks = HookHandler.getHooks(type);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			if (hook.getPlayerName().equalsIgnoreCase(player.getName())) hook.execute();
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		IDebug debug = EventEngine.Debugger;
		debug.debugFiner(
			"Setting block on thread #%d %s",
			Thread.currentThread().getId(), Thread.currentThread().getName()
		);
		debug.debugFine("Interact event detected");
		List<Hook> hooks = HookHandler.getHooks(HookType.INTERACT);

		if (hooks == null || hooks.isEmpty()) return;

		debug.debugFine("Hooks not null");
		for (Hook hook : hooks)
		{
			debug.debugFine("Processing hook...");
			IBlock block = event.getBlock();
			if (hook.getData() != null)
				if (block == null || block.getMaterial().getItemID() != (Integer) hook.getData()) continue;

			debug.debugFine("Block is not null");

			IWorld hookWorld = hook.getWorld();
			ILocation location = block.getLocation();
			if (hookWorld == null)
			{
				debug.debugFine("Hook world is null, using location");
				if (!location.getWorld().getName().equals(hook.getLocation().getWorld().getName()))
				{
					return;
				}
				debug.debugFine("Correct world!");
				if (!(location.distance(hook.getLocation()) < 1)) return;
				debug.debugFine("Distance is less than 1");
				LuaTable table = new LuaTable();
				if (event.getPlayer() != null) table.set("player", LuaValue.valueOf(event.getPlayer().getName()));

				table.set("x", LuaValue.valueOf(location.getBlockX()));
				table.set("y", LuaValue.valueOf(location.getBlockY()));
				table.set("z", LuaValue.valueOf(location.getBlockZ()));
				table.set("blockID", LuaValue.valueOf(block.getMaterial().getItemID()));
				table.set("blockData", LuaValue.valueOf((block).getData()));

				hook.execute(table);
				return;
			}
			if (!hookWorld.getName().equals(block.getWorld().getName())) continue;

			debug.debugFine("Hook world is not null, sending location data");
			LuaTable table = new LuaTable();
			if (event.getPlayer() != null) table.set("player", LuaValue.valueOf(event.getPlayer().getName()));

			table.set("x", LuaValue.valueOf(location.getBlockX()));
			table.set("y", LuaValue.valueOf(location.getBlockY()));
			table.set("z", LuaValue.valueOf(location.getBlockZ()));
			table.set("blockID", LuaValue.valueOf(block.getMaterial().getItemID()));
			table.set("blockData", LuaValue.valueOf((block).getData()));

			hook.execute(table);
		}
	}

	@Override
	public void OnBlockRedstoneEvent(RunsafeBlockRedstoneEvent event)
	{
		if (event.getNewCurrent() <= 0 || event.getOldCurrent() != 0) return;

		List<Hook> hooks = HookHandler.getHooks(HookType.BLOCK_GAINS_CURRENT);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
		{
			IBlock block = event.getBlock();
			if (block == null) continue;
			ILocation location = block.getLocation();
			if (location.getWorld().getName().equals(hook.getLocation().getWorld().getName()))
				if (location.distance(hook.getLocation()) < 1) hook.execute();
		}
	}

	@Override
	public boolean OnBlockBreak(IPlayer player, IBlock block)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.BLOCK_BREAK);
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

			hook.execute(table);
		}
		return true;
	}

	@Override
	public void OnPlayerLeftClick(RunsafePlayerClickEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.LEFT_CLICK_BLOCK);
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

			hook.execute(table);
		}
	}

	@Override
	public void OnPlayerDamage(IPlayer player, RunsafeEntityDamageEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.PLAYER_DAMAGE);
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

			hook.execute(table);
		}
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.PLAYER_ITEM_DROP);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			handleItemHook(hook, event.getPlayer(), event.getItem().getItemStack());
	}

	@Override
	public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event)
	{
		List<Hook> hooks = HookHandler.getHooks(HookType.PLAYER_ITEM_PICKUP);
		if (hooks == null || hooks.isEmpty()) return;

		for (Hook hook : hooks)
			handleItemHook(hook, event.getPlayer(), event.getItem().getItemStack());
	}

	private void handleItemHook(Hook hook, IPlayer player, RunsafeMeta item)
	{
		IWorld hookWorld = hook.getWorld();
		if (!hookWorld.isWorld(player.getWorld())) return;
		LuaTable table = new LuaTable();
		table.set("player", player.getName());
		table.set("itemID", item.getItemId());
		table.set("itemName", item.hasDisplayName() ? item.getDisplayName() : item.getNormalName());

		hook.execute(table);
	}

	private static final HashMap<HookType, List<Hook>> hooks = new HashMap<>();
	private static String context;
}
