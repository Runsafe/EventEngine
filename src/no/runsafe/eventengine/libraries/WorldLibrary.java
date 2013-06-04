package no.runsafe.eventengine.libraries;

import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.chunk.RunsafeChunk;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class WorldLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("setBlock", new SetBlock());
		lib.set("getBlock", new GetBlock());

		env.get("engine").set("world", lib);
		return lib;
	}

	static class SetBlock extends VarArgFunction
	{
		// world, x, y, z, blockID, [blockData]
		public Varargs invoke(Varargs args)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(args.checkstring(1));
			if (world == null) return null;

			RunsafeLocation location = new RunsafeLocation(world, args.checkdouble(2), args.checkdouble(3), args.checkdouble(4));

			RunsafeChunk chunk = location.getChunk();
			if (!chunk.isLoaded())
				chunk.load();

			RunsafeBlock block = location.getBlock();
			block.setTypeId(args.checkint(5));

			if (args.isnumber(6))
				block.setData((byte) args.checkint(6));

			return null;
		}
	}

	static class GetBlock extends VarArgFunction
	{
		// world, x, y, z
		public Varargs invoke(Varargs args)
		{
			RunsafeWorld world = ObjectLibrary.getWorld(args.checkstring(1));
			if (world == null) return null;

			RunsafeLocation location = new RunsafeLocation(world, args.checkdouble(2), args.checkdouble(3), args.checkdouble(4));

			RunsafeChunk chunk = location.getChunk();
			if (!chunk.isLoaded())
				chunk.load();

			RunsafeBlock block = location.getBlock();
			LuaValue[] data = {valueOf(block.getTypeId()), valueOf(block.getData())};
			return varargsOf(data);
		}
	}
}
