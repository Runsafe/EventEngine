package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.RunsafeLuaFunction;
import no.runsafe.framework.api.lua.VoidFunction;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.chunk.RunsafeChunk;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

public class WorldLibrary extends Library
{
	public WorldLibrary(RunsafePlugin plugin)
	{
		super(plugin, "world");
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("setBlock", new SetBlock());
		lib.set("getBlock", new GetBlock());
		lib.set("getPlayers", new GetPlayers());
		return lib;
	}

	private static void prepareLocationForEdit(RunsafeLocation location)
	{
		RunsafeChunk chunk = location.getChunk();
		if (chunk.isUnloaded()) chunk.load();
	}

	private static class SetBlock extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			RunsafeLocation location = parameters.getLocation(0);
			WorldLibrary.prepareLocationForEdit(location);

			IBlock block = location.getBlock();
			block.set(Item.get(parameters.getInt(4)));

			if (parameters.hasParameter(5))
				block.setData((byte) (int) parameters.getInt(5));
		}
	}

	private static class GetBlock extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<Object>();
			RunsafeLocation location = parameters.getLocation(0);
			WorldLibrary.prepareLocationForEdit(location);

			IBlock block = location.getBlock();
			returns.add(block.getMaterial().getTypeID());
			returns.add(block.getData());

			return returns;
		}
	}

	private static class GetPlayers extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returns = new ArrayList<Object>();
			RunsafeWorld world = parameters.getWorld(0);

			for (IPlayer player : world.getPlayers())
				returns.add(player.getName());

			return returns;
		}
	}
}
