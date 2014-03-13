package no.runsafe.eventengine.entity;

public class EntityGoal
{
	public EntityGoal(double x, double y, double z, float speed)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.speed = speed;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public float getSpeed()
	{
		return speed;
	}

	private final double x;
	private final double y;
	private final double z;
	private final float speed;
}
