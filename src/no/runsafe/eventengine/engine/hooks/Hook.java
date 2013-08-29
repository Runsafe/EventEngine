package no.runsafe.eventengine.engine.hooks;

import no.runsafe.framework.internal.lua.Environment;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import org.luaj.vm2.LuaTable;

public class Hook
{
	public Hook(HookType type, String function)
	{
		this.type = type;
		this.function = function;
	}

	public HookType getType()
	{
		return this.type;
	}

	String getFunction()
	{
		return this.function;
	}

	public RunsafeLocation getLocation()
	{
		return this.location;
	}

	public void setLocation(RunsafeLocation location)
	{
		this.location = location;
	}

	public void setWorld(RunsafeWorld world)
	{
		this.world = world;
	}

	public RunsafeWorld getWorld()
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
			Environment.global.get(this.getFunction()).call(arguments);
		else
			Environment.global.get(this.getFunction()).call();
	}

	private final HookType type;
	private final String function;
	private RunsafeLocation location;
	private RunsafeWorld world;
	private String playerName;
	private Object data;
}