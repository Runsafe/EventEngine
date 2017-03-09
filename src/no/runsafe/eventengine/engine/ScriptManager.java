package no.runsafe.eventengine.engine;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import no.runsafe.eventengine.EventEngine;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.lua.IGlobal;
import org.bukkit.util.FileUtil;
import org.luaj.vm2.LuaError;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ScriptManager implements IPluginEnabled
{
	public ScriptManager(EventEngine eventEngine, IGlobal environment, IConsole output, IScheduler scheduler)
	{
		this.environment = environment;
		this.scheduler = scheduler;
		scriptPath = new File(eventEngine.getDataFolder(), "scripts");
		if (!scriptPath.exists())
			if (scriptPath.mkdirs())
				output.logWarning("Failed to create scripts directory at: " + scriptPath.getPath());

		this.output = output;
	}

	@Override
	public void OnPluginEnabled()
	{
		scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				loadScripts();
			}
		}, 1);
	}

	private void loadScripts()
	{
		int succeeded = 0;
		int failed = 0;

		File loadList = FileUtils.getFile(scriptPath, "scripts.list");

		if (loadList.exists())
		{
			try
			{
				List<String> scripts = Files.readLines(loadList, Charsets.UTF_8);

				for (String script : scripts)
				{
					String output = this.runScript(FileUtils.getFile(scriptPath, script));
					if (output != null)
					{
						this.output.logError(output);
						failed++;
					}
					else
					{
						this.output.logInformation("Script loaded: " + script);
						succeeded++;
					}
				}
			}
			catch (IOException e)
			{
				this.output.logWarning("Unable to read content from script list.");
			}
		}
		else
		{
			this.output.logWarning("No script list file found in the scripts bin.");
		}

		this.output.logInformation("%d lua script(s) loaded.", succeeded);
		if (failed > 0)
			this.output.logError("%d lua script(s) failed to load.", failed);
	}

	private String runScript(File script)
	{
		if (!script.exists() || !script.isFile())
			return null;

		try
		{
			environment.loadFile(script.getAbsolutePath());
		}
		catch (LuaError error)
		{
			return "Lua Error: " + error.getMessage();
		}
		return null;
	}

	private final IGlobal environment;
	private final File scriptPath;
	private final IConsole output;
	private final IScheduler scheduler;
}
