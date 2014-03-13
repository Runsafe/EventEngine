package no.runsafe.eventengine.entity;

import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.PathfinderGoal;

public class PathfinderGoToTarget extends PathfinderGoal
{
	public PathfinderGoToTarget(EntityInsentient entity)
	{
		this.entity = entity;
	}

	@Override
	public boolean a()
	{
		return entity.aN() < 100;
	}

	@Override
	public void c()
	{
		EntityGoal goal = EntityDriver.getTarget(entity.getId());
		entity.getNavigation().a(goal.getX(), goal.getY(), goal.getZ(), goal.getSpeed());
	}

	private final EntityInsentient entity;
}
