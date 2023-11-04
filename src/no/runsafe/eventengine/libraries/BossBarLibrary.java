package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IBossBar;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.RunsafeLuaFunction;
import no.runsafe.framework.api.lua.VoidFunction;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.bossBar.*;

import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BossBarLibrary extends Library
{
	public BossBarLibrary(RunsafePlugin plugin)
	{
		super(plugin, "bossbar");
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("createBossBar", new CreateBossBar());
		lib.set("removeBossBar", new RemoveBossBar());
		lib.set("setTitle", new SetTitle());
		lib.set("getTitle", new GetTitle());
		lib.set("setProgress", new SetProgress());
		lib.set("getProgress", new GetProgress());
		lib.set("addFlag", new AddFlag());
		lib.set("hasFlag", new HasFlag());
		lib.set("removeFlag", new RemoveFlag());
		lib.set("setStyle", new SetStyle());
		lib.set("setColour", new SetColour());
		lib.set("addPlayer", new AddPlayer());
		lib.set("removePlayer", new RemovePlayer());
		lib.set("removeAllPlayers", new RemoveAllPlayers());
		lib.set("getPlayerList", new GetPlayerList());
		lib.set("setVisible", new SetVisible());
		lib.set("isVisible", new IsVisible());
		return lib;
	}

	/**
	 * Creates a new boss bar.
	 * Returns: the new boss bar ID
	 */
	private static class CreateBossBar extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			String barName = parameters.getString(0);
			bossBarList.put(currentBossBarID, new RunsafeBossBar(barName, BarColour.PURPLE, BarStyle.SOLID));

			List<Object> returns = new ArrayList<>();
			returns.add(currentBossBarID++);
			return returns;
		}
	}

	/**
	 * Deletes a boss bar based on its ID.
	 * Stops players from being able to see it first, so it doesn't get stuck on their screens.
	 * Parameter 0: int: Bar ID
	 */
	private static class RemoveBossBar extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			int barID = parameters.getInt(0);
			bossBarList.get(barID).removeAllPlayers();
			bossBarList.remove(barID);
		}
	}

	/**
	 * Sets a boss bar title.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: title
	 */
	private static class SetTitle extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).setTitle(parameters.getString(1));
		}
	}

	/**
	 * Gets a boss bar title.
	 * Parameter 0: int: Bar ID
	 * Returns: String: title
	 */
	private static class GetTitle extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<>();
			returns.add(bossBarList.get(parameters.getInt(0)).getTitle());
			return returns;
		}
	}

	/**
	 * Sets how filled the boss bar should be.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: double: between 0.0 and 1.0
	 */
	private static class SetProgress extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).setProgress(parameters.getDouble(1));
		}
	}

	/**
	 * Gets how filled the boss bar should be.
	 * Parameter 0: int: Bar ID
	 * Returns: double: between 0.0 and 1.0
	 */
	private static class GetProgress extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<>();
			returns.add(bossBarList.get(parameters.getInt(0)).getProgress());
			return returns;
		}
	}

	/**
	 * Sets a boss bar flag to true.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: name of flag
	 */
	private static class AddFlag extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).addFlag(barFlags.get(parameters.getString(1)));
		}
	}

	/**
	 * Get state of a boss bar flag.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: name of flag
	 */
	private static class HasFlag extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<>();
			returns.add(bossBarList.get(parameters.getInt(0)).hasFlag(barFlags.get(parameters.getString(1))));
			return returns;
		}
	}

	/**
	 * Sets a boss bar flag to false.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: name of flag
	 */
	private static class RemoveFlag extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).removeFlag(barFlags.get(parameters.getString(1)));
		}
	}

	/**
	 * Sets the boss bar style.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: name of bar style
	 */
	private static class SetStyle extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).setStyle(barStyles.get(parameters.getString(1)));
		}
	}

	/**
	 * Sets the boss bar colour.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: String: name of colour
	 */
	private static class SetColour extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).setColour(barColours.get(parameters.getString(1)));
		}
	}

	/**
	 * Adds a player to the boss bar, so they can see it.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: Player name
	 */
	private static class AddPlayer extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).addPlayer(parameters.getPlayer(1));
		}
	}

	/**
	 * Removes a player from the boss bar, so they can't see it.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: Player name
	 */
	private static class RemovePlayer extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).removePlayer(parameters.getPlayer(1));
		}
	}

	/**
	 * Gets a list of players that can see this boss bar.
	 * Parameter 0: int: Bar ID
	 * Returns: List of player names as Strings
	 */
	private static class GetPlayerList extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<>();
			for (IPlayer player : bossBarList.get(parameters.getInt(0)).getPlayers())
				returns.add(player.getName());
			return returns;
		}
	}

	/**
	 * Remove all players from this boss bar.
	 * Parameter 0: int: Bar ID
	 */
	private static class RemoveAllPlayers extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).removeAllPlayers();
		}
	}

	/**
	 * Set whether players added to this boss bar can see it.
	 * Parameter 0: int: Bar ID
	 * Parameter 1: bool: true if boss bar should be visible, false if not
	 */
	private static class SetVisible extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			bossBarList.get(parameters.getInt(0)).setVisible(parameters.getBool(1));
		}
	}

	/**
	 * Check if players can see the boss bar.
	 * Parameter 0: int: Bar ID
	 * Returns: Bool: true if boss bar is visible, false if not
	 */
	private static class IsVisible extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<>();
			returns.add(bossBarList.get(parameters.getInt(0)).isVisible());
			return returns;
		}
	}

	private static int currentBossBarID = 1;
	private static final HashMap<Integer, IBossBar> bossBarList = new HashMap<>();

	private static final HashMap<String, BarColour> barColours = new HashMap<>();
	private static final HashMap<String, BarStyle> barStyles = new HashMap<>();
	private static final HashMap<String, BarFlag> barFlags = new HashMap<>();

	static
	{
		BossBarLibrary.barColours.put("blue", BarColour.BLUE);
		BossBarLibrary.barColours.put("green", BarColour.GREEN);
		BossBarLibrary.barColours.put("pink", BarColour.PINK);
		BossBarLibrary.barColours.put("purple", BarColour.PURPLE);
		BossBarLibrary.barColours.put("red", BarColour.RED);
		BossBarLibrary.barColours.put("white", BarColour.WHITE);
		BossBarLibrary.barColours.put("yellow", BarColour.YELLOW);

		BossBarLibrary.barStyles.put("solid", BarStyle.SOLID);
		BossBarLibrary.barStyles.put("segmented6", BarStyle.SEGMENTED_6);
		BossBarLibrary.barStyles.put("segmented10", BarStyle.SEGMENTED_10);
		BossBarLibrary.barStyles.put("segmented12", BarStyle.SEGMENTED_12);
		BossBarLibrary.barStyles.put("segmented20", BarStyle.SEGMENTED_20);

		BossBarLibrary.barFlags.put("fog", BarFlag.FOG);
		BossBarLibrary.barFlags.put("darkenSky", BarFlag.DARKEN_SKY);
		BossBarLibrary.barFlags.put("dragonBossAmbience", BarFlag.DRAGON_BOSS_AMBIENCE);
	}
}
