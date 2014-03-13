package no.runsafe.eventengine.entity;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.entity.IEntity;

import java.util.HashMap;

public class EntityDriver
{
	public static void addTarget(IEntity entity, ILocation location, float speed)
	{
		targets.put(entity.getEntityId(), new EntityGoal(location.getX(), location.getY(), location.getZ(), speed));
	}

	public static EntityGoal getTarget(int entityID)
	{
		return targets.containsKey(entityID) ? targets.get(entityID) : null;
	}

	private static HashMap<Integer, EntityGoal> targets = new HashMap<Integer, EntityGoal>(0);
}
