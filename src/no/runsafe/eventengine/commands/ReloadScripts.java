package no.runsafe.eventengine.commands;

import no.runsafe.eventengine.engine.ScriptManager;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class ReloadScripts extends ExecutableCommand
{
	public ReloadScripts(IOutput output, ScriptManager scriptManager)
	{
		super("reloadscripts", "Reloads all lua scripts.", "runsafe.eventengine.reload");
		this.output = output;
		this.scriptManager = scriptManager;
	}

	@Override
	protected String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		this.output.logWarning("Reloading lua engine.");
		HookHandler.clearHooks(); // Clear any hooks scripts have made.
		this.scriptManager.OnPluginEnabled(); // Clear environment and reload all scripts.
		return (executor instanceof RunsafePlayer ? "&eReloaded!" : null);
	}

	private final IOutput output;
	private final ScriptManager scriptManager;
}
