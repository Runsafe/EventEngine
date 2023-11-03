package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import org.luaj.vm2.LuaTable;

public class DebugLibrary extends Library
{
	/*
		#LUADOC
		@library Debug
	 */
	public DebugLibrary(RunsafePlugin plugin, IDebug debugger)
	{
		super(plugin, "debug");
		debug = debugger;
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("severe", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugSevere(parameters.getString(0));
			}
		});
		lib.set("warning", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugWarning(parameters.getString(0));
			}
		});
		lib.set("info", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugInfo(parameters.getString(0));
			}
		});
		lib.set("fine", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugFine(parameters.getString(0));
			}
		});
		lib.set("finer", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugFiner(parameters.getString(0));
			}
		});
		lib.set("finest", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugFinest(parameters.getString(0));
			}
		});
		lib.set("config", new VoidFunction()
		{
			@Override
			public void run(FunctionParameters parameters)
			{
				debug.debugConfig(parameters.getString(0));
			}
		});
		return lib;
	}

	private static IDebug debug;

}
