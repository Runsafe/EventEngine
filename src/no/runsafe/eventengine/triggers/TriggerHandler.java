package no.runsafe.eventengine.triggers;

import no.runsafe.eventengine.engine.ScriptRunner;
import no.runsafe.framework.minecraft.RunsafeLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TriggerHandler
{
	public TriggerHandler(ScriptRunner scriptRunner, TriggerRepository repository)
	{
		this.scriptRunner = scriptRunner;
		this.repository = repository;

		// Load triggers from DB
		this.triggers = this.repository.getTriggers();
	}

	public void trigger(RunsafeLocation location)
	{
		String worldName = location.getWorld().getName();
		if (this.triggers.containsKey(worldName))
			for (Trigger trigger : this.triggers.get(worldName))
				if (trigger.getLocation().distance(location) < 1)
					if (trigger.getLocation().getWorld().getName().equals(worldName))
					this.scriptRunner.runScript(trigger.getScriptFile());
	}


	public void deleteTriggersAtLocation(RunsafeLocation location)
	{
		String worldName = location.getWorld().getName();
		if (triggers.containsKey(worldName))
		{
			for (Trigger trigger : triggers.get(worldName))
			{
				if (trigger.getLocation().getWorld().getName().equals(worldName))
				{
					if (trigger.getLocation().distance(location) < 1)
					{
						this.repository.deleteTriggers(location);
						return;
					}
				}
			}
		}
	}

	public void addTrigger(RunsafeLocation location, String scriptFile)
	{
		String worldName = location.getWorld().getName();
		if (!this.triggers.containsKey(worldName))
			this.triggers.put(worldName, new ArrayList<Trigger>());

		this.triggers.get(worldName).add(new Trigger(location, scriptFile));
		this.repository.addTrigger(location, scriptFile);
	}

	private final ScriptRunner scriptRunner;
	private final TriggerRepository repository;
	private HashMap<String, List<Trigger>> triggers = new HashMap<String, List<Trigger>>();
}
