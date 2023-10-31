package no.runsafe.eventengine.engine.hooks;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.lua.IGlobal;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Hook
{
	public Hook(HookType type, String function, IGlobal environment, Logger logger)
	{
		this.type = type;
		this.function = function;
		this.environment = environment;
		this.logger = logger;
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

	public void execute()
	{
		this.execute(null);
	}

	public void execute(LuaTable arguments)
	{
		LuaValue handler = getHandler();
		if (handler == null)
		{
			return;
		}
		try
		{
			if (arguments != null)
			{
				handler.call(arguments);
				return;
			}
			handler.call();
		}
		catch (LuaError error)
		{
			this.logger.log(
				Level.WARNING,
				"LuaError: %s in event hook %s",
				new Object[]
				{
					error.getMessage(),
					getFunction()
				}
			);
		}
	}

	public LuaValue getHandler()
	{
		String scriptFunction = getFunction();
		boolean isStringFunction = scriptFunction.startsWith("return ");
		try
		{
            return isStringFunction
					? environment.get("dostring").call(scriptFunction)
					: environment.get(scriptFunction);
		}
		catch(LuaError e)
		{
			logger.log(
				Level.WARNING,
				"LuaError %s trying to get Lua event handler script %s",
				new Object[]{ e.getMessage(), scriptFunction }
			);
			return null;
		}
	}

	private final HookType type;
	private final String function;
	private final IGlobal environment;
	private final Logger logger;
	private ILocation location;
	private IWorld world;
	private String playerName;
	private Object data;
}