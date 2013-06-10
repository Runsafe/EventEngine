package no.runsafe.eventengine.triggers;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerRepository extends Repository
{
	public TriggerRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "event_triggers";
	}

	public void addTrigger(RunsafeLocation location, String script)
	{
		this.database.Execute(
				"INSERT INTO `event_triggers` (world, x, y, z, script) VALUES(?, ?, ?, ?, ?)",
				location.getWorld().getName(),
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ(),
				script
		);
	}

	public void deleteTriggers(RunsafeLocation location)
	{
		this.database.Execute(
				"DELETE FROM `event_triggers` WHERE world = ? AND x = ? AND y = ? AND z = ?",
				location.getWorld().getName(),
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
		);
	}

	public HashMap<String, List<Trigger>> getTriggers()
	{
		HashMap<String, List<Trigger>> triggers = new HashMap<String, List<Trigger>>();
		List<Map<String, Object>> data = this.database.Query("SELECT world, x, y, z, script FROM `event_triggers`");

		if (data != null)
		{
			for (Map<String, Object> node : data)
			{
				String worldName = (String) node.get("world");
				RunsafeWorld world = RunsafeServer.Instance.getWorld(worldName);

				if (world != null)
				{
					if (!triggers.containsKey(worldName))
						triggers.put(worldName, new ArrayList<Trigger>());

					triggers.get(worldName).add(new Trigger(new RunsafeLocation(
							world,
							getDoubleValue(node, "x"),
							getDoubleValue(node, "y"),
							getDoubleValue(node, "z")
					), (String) node.get("script")));
				}
			}
		}
		return triggers;
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> versions = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
				"CREATE TABLE `event_triggers` (" +
						"`world` VARCHAR(50) NOT NULL," +
						"`x` DOUBLE NOT NULL," +
						"`y` DOUBLE NOT NULL," +
						"`z` DOUBLE NOT NULL," +
						"`script` VARCHAR(50) NOT NULL" +
					")"
		);
		versions.put(1, sql);

		return versions;
	}

	private final IDatabase database;
}
