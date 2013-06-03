package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.framework.RunsafePlugin;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RunScript.class);
	}
}
