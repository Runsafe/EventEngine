package no.runsafe.eventengine.engine;

import no.runsafe.eventengine.Plugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ScriptManager implements IPluginEnabled
{
	public ScriptManager(Plugin eventEngine, IOutput output)
	{
		// Setup folders
		this.path = String.format("plugins/%s/scripts/", eventEngine.getName());
		File pathCheck = new File(this.path);

		if (!pathCheck.exists())
			if (pathCheck.mkdirs())
				output.warning("Failed to create scripts directory at: " + this.path);

		this.output = output;
	}

	@Override
	public void OnPluginEnabled()
	{
		this.loadEngine();
	}

	private void loadEngine()
	{
		try
		{
			String engineFile = this.path + "engine.lua";
			InputStream input = ScriptManager.class.getResourceAsStream("/engine.lua");
			OutputStream output = new FileOutputStream(engineFile);

			byte[] buffer = new byte[4096];
			int length;
			while ((length = input.read(buffer)) > 0)
				output.write(buffer, 0, length);

			output.close();
			input.close();

			Environment.global.get("dofile").call(LuaValue.valueOf(engineFile));
		}
		catch (Exception e)
		{
			this.output.logError("Unable to write engine file, exception below.");
			this.output.logException(e);
			return;
		}
		this.loadScripts();
	}

	private void loadScripts()
	{
		int succeeded = 0;
		int failed = 0;

		File[] files = new File(this.path).listFiles();

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
		String file = path + script;

		if (!new File(file).exists())
			return "Unable to find script: " + file;

		try
		{
			Environment.global.get("dofile").call(LuaValue.valueOf(file));
		}
		catch (LuaError error)
		{
			return "Lua Error: " + error.getMessage();
		}
		return null;
	}

	private final String path;
	private IOutput output;
}
