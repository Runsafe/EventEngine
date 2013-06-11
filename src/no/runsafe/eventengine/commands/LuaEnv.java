package no.runsafe.eventengine.commands;

import no.runsafe.eventengine.engine.Environment;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.internal.command.ExecutableCommand;

import java.util.HashMap;

public class LuaEnv extends ExecutableCommand
{
	public LuaEnv()
	{
		super("luaenv", "Sets a global Lua environment value", "runsafe.eventengine.luaenv", "key", "value");
	}

	@Override
	public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		Environment.global.set(parameters.get("key"), parameters.get("value"));
		return "&2Environment value set.";
	}
}
