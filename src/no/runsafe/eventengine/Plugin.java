package no.runsafe.eventengine;

import no.runsafe.eventengine.commands.RunScript;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.output.IOutput;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(RunScript.class);
		Plugin.console = this.output;
	}

	public static IOutput console;
}
