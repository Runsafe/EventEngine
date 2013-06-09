package no.runsafe.eventengine.engine;

import no.runsafe.eventengine.Plugin;
import no.runsafe.framework.output.IOutput;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.File;

public class ScriptRunner
{
	public ScriptRunner(Plugin eventEngine, IOutput output)
	{
		// Setup folders
		this.path = String.format("plugins/%s/scripts/", eventEngine.getName());
		if (!new File(this.path).mkdirs())
			output.warning("Failed to create scripts directory at: " + this.path);
	}

	public String runScript(String script)
	{
		String file = path + script + ".lua";

		if (!new File(file).exists())
			return "&cScript not found.";

		try
		{
			Environment.global.get("dofile").call(LuaValue.valueOf(file));
		}
		catch (LuaError error)
		{
			return "&c" + error.getMessage();
		}

		return "&2Script executed.";
	}

	private final String path;
}
