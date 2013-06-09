package no.runsafe.eventengine.triggers;

import no.runsafe.framework.server.RunsafeLocation;

public class Trigger
{
	public Trigger(RunsafeLocation location, String scriptFile)
	{
		this.location = location;
		this.scriptFile = scriptFile;
	}

	public RunsafeLocation getLocation()
	{
		return this.location;
	}

	public String getScriptFile()
	{
		return this.scriptFile;
	}

	private final RunsafeLocation location;
	private final String scriptFile;
}
