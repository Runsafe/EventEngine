package no.runsafe.eventengine.engine.hooks;

import no.runsafe.eventengine.engine.Environment;
import no.runsafe.framework.minecraft.RunsafeLocation;

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

	public String getFunction()
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
		Environment.global.get(this.getFunction()).call();
	}

	private HookType type;
	private String function;
	private RunsafeLocation location;
	private String playerName;
	private Object data;
}
