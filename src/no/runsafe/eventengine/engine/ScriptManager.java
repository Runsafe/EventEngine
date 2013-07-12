package no.runsafe.eventengine.engine;

import no.runsafe.eventengine.Plugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.lua.LuaEnvironment;
import org.luaj.vm2.LuaError;

import java.io.File;

public class ScriptManager implements IPluginEnabled
{
	public ScriptManager(Plugin eventEngine, IOutput output)
	{
		// Setup folders
		this.path = String.format("plugins/%s/", eventEngine.getName());
		this.scriptPath = this.path + "scripts/";

		File pathCheck = new File(this.scriptPath);
		if (!pathCheck.exists())
			if (pathCheck.mkdirs())
				output.warning("Failed to create scripts directory at: " + this.scriptPath);

		this.output = output;
	}

	@Override
	public void OnPluginEnabled()
	{
		this.loadScripts();
	}

	private void loadScripts()
	{
		int succeeded = 0;
		int failed = 0;

		File[] files = new File(this.scriptPath).listFiles();

		if (files != null)
		{
			for (File file : files)
			{
				String fileName = file.getName();
				if (file.isFile() && fileName.endsWith(".lua"))
				{
					String output = this.runScript(fileName);
					if (output != null)
					{
						this.output.logError(output);
						failed += 1;
					}
					else
					{
						succeeded += 1;
					}
				}
			}
		}

		this.output.logInformation("%d lua scripts loaded.", succeeded);

		if (failed > 0)
			this.output.logError("%d lua scripts failed to load.", failed);
	}

	private String runScript(String script)
	{
		String file = scriptPath + script;

		if (!new File(file).exists())
			return "Unable to find script: " + file;

		try
		{
			LuaEnvironment.loadFile(file);
		}
		catch (LuaError error)
		{
			return "Lua Error: " + error.getMessage();
		}
		return null;
	}

	private final String path;
	private final String scriptPath;
	private final IOutput output;
}
