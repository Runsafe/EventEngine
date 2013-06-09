package no.runsafe.eventengine.commands;

import no.runsafe.eventengine.engine.ScriptRunner;
import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.server.ICommandExecutor;

import java.util.HashMap;

public class RunScript extends ExecutableCommand
{
	public RunScript(ScriptRunner scriptRunner)
	{
		super("run", "Executes an LUA script", "runsafe.eventengine.run", "script");
		this.scriptRunner = scriptRunner;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		return this.scriptRunner.runScript(parameters.get("script"));
	}

	private final ScriptRunner scriptRunner;
}
