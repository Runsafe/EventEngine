package no.runsafe.eventengine.commands;

import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.server.ICommandExecutor;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.HashMap;

public class RunScript extends ExecutableCommand
{
	public RunScript(Plugin eventEngine)
	{
		super("run", "Executes an LUA script", "runsafe.eventengine.run", "script");
		this.path = String.format("plugins/%s/scripts/", eventEngine.getName());
		new File(this.path).mkdirs();
	}

	@Override
	public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters)
	{
		String file = path + parameters.get("script") + ".lua";

		if (!new File(file).exists())
			return "&cScript not found.";

		JsePlatform.standardGlobals().get("dofile").call(LuaValue.valueOf(file));
		return "&2Script executed.";
	}

	private String path;
}
