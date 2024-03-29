package no.runsafe.eventengine.engine.hooks;

import no.runsafe.eventengine.EventEngine;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.lua.IGlobal;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Hook
{
	public Hook(HookType type, String function, IGlobal environment, Logger logger)
	{
		this.type = type;
		this.function = function;
		this.environment = environment;
		this.logger = logger;
	}

	public Runnable getExecutor(LuaTable arguments)
	{
		return new Executor(arguments);
	}

	private final class Executor implements Runnable
	{
		public Executor(LuaTable arguments)
		{
			this.arguments = arguments;
		}

		@Override
		public void run()
		{
			EventEngine.Debugger.debugFiner(
				"Executing hook on thread #%d %s",
				Thread.currentThread().getId(),
				Thread.currentThread().getName()
			);
			LuaValue handler = getHandler();
			if (handler == null)
			{
				EventEngine.Debugger.debugFiner("There is no handler, not invoking hook");
				return;
			}
			try
			{
				if (arguments != null)
				{
					EventEngine.Debugger.debugFine("Invoking hook with arguments: %s", arguments);
					handler.call(arguments);
					return;
				}
				EventEngine.Debugger.debugFine("Invoking hook without arguments");
				handler.call();
			}
			catch (LuaError error)
			{
				logger.log(
					Level.WARNING,
					"LuaError: @" + context + " " + error.getMessage() + " in event hook " + getFunction()
				);
			}
		}

		private LuaValue getHandler()
		{
			String scriptFunction = getFunction();
			boolean isStringFunction = scriptFunction.startsWith("return ");
			try
			{
				return isStringFunction
					? environment.get("dostring").call(scriptFunction)
					: environment.get(scriptFunction);
			}
			catch (LuaError e)
			{
				logger.log(
					Level.WARNING,
					"LuaError " + e.getMessage() + " trying to get Lua event handler script"
				);
				return null;
			}
		}

		private final LuaTable arguments;
	}

	public HookType getType()
	{
		return this.type;
	}

	String getFunction()
	{
		return this.function;
	}

	public ILocation getLocation()
	{
		return this.location;
	}

	public void setLocation(ILocation location)
	{
		this.location = location;
	}

	public void setWorld(IWorld world)
	{
		this.world = world;
	}

	public IWorld getWorld()
	{
		if (this.world == null && location != null)
		{
			return location.getWorld();
		}
		return this.world;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public Object getData()
	{
		return this.data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public void setContext(String context)
	{
		this.context = context == null ? "(unknown)" : context;
	}

	private final HookType type;
	private final String function;
	private final IGlobal environment;
	private final Logger logger;
	private ILocation location;
	private IWorld world;
	private String playerName;
	private Object data;
	private String context = "(unspecified)";
}