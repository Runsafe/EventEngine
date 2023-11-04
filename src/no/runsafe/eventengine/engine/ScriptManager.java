package no.runsafe.eventengine.engine;

import com.google.common.base.Strings;
import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.lua.IGlobal;
import org.apache.commons.io.FileUtils;
import org.luaj.vm2.LuaError;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		scheduler.startSyncTask(this::loadScripts, 1);
	}

	private void loadScripts()
	{
		List<File> scripts = readFileList();
		if (scripts == null)
		{
			return;
		}

		int succeeded = 0;
		for (File file : scripts)
		{
			if (this.runScript(file))
			{
				succeeded++;
			}
		}

		int failed = scripts.size() - succeeded;
		this.output.logInformation("%d lua script(s) loaded.", succeeded);
		if (failed > 0)
		{
			this.output.logError("%d lua script(s) failed to load.", failed);
		}
	}

	@Nullable
	private List<File> readFileList()
	{
		try
		{
			File loadList = FileUtils.getFile(scriptPath, "scripts.list");
			if (loadList == null || !loadList.exists())
			{
				this.output.logWarning("No script list file found in the scripts bin.");
				return null;
			}

			List<File> scripts = new ArrayList<>();
			FileReader fileList = new FileReader(loadList);
			BufferedReader reader = new BufferedReader(fileList);
			String line = reader.readLine();
			while (line != null)
			{
				String script = line.trim();
				if (!Strings.isNullOrEmpty(script) && !script.startsWith("#"))
				{
					File file = probeFile(script);
					if (file != null)
					{
						scripts.add(file);
					}
				}
				line = reader.readLine();
			}
			return scripts;
		}
		catch (IOException e)
		{
			this.output.logWarning("Unable to read content from script list.");
			return null;
		}
	}

	@Nullable
	private File probeFile(String script)
	{
		try
		{
			File file = FileUtils.getFile(scriptPath, script);
			if (file == null || !file.exists())
			{
				this.output.logWarning("Script not found: " + script);
				return null;
			}
			return file;
		}
		catch (Exception e)
		{
			this.output.logWarning("Unable to read " + script + ": " + e.getMessage());
			return null;
		}
	}

	private boolean runScript(File script)
	{
		try
		{
			this.output.logInformation("Loading script: " + script.getName());
			HookHandler.setContext(script.getAbsolutePath());
			environment.loadFile(script.getAbsolutePath());
			HookHandler.setContext(null);
			return true;
		}
		catch (LuaError error)
		{
			this.output.logError("Lua Error: " + error.getMessage());
			return false;
		}
	}

	private final IGlobal environment;
	private final File scriptPath;
	private final IConsole output;
	private final IScheduler scheduler;
}
