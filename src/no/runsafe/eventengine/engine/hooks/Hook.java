package no.runsafe.eventengine.engine.hooks;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.lua.IGlobal;
import org.luaj.vm2.LuaTable;

public class Hook
{
	public Hook(HookType type, String function, IGlobal environment)
	{
		this.type = type;
		this.function = function;
		this.environment = environment;
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
		if (arguments != null)
			environment.get(this.getFunction()).call(arguments);
		else
			environment.get(this.getFunction()).call();
	}

	private final HookType type;
	private final String function;
	private final IGlobal environment;
	private ILocation location;
	private IWorld world;
	private String playerName;
	private Object data;
}