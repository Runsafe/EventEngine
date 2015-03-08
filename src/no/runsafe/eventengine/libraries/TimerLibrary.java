package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.IntegerFunction;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

public class TimerLibrary extends Library
{
	public TimerLibrary(RunsafePlugin plugin, IScheduler scheduler)
	{
		super(plugin, "timer");
		TimerLibrary.scheduler = scheduler;
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();

		lib.set("scheduleTask", new IntegerFunction() {
			@Override
			public Integer run(final FunctionParameters parameters) {
				int timerID = scheduler.startSyncTask(new Runnable() {
					@Override
					public void run() {
						globals.get(parameters.getString(0)).call();
					}
				}, (long) parameters.getInt(1));

				timers.add(timerID);

				return timerID;
			}
		});

		lib.set("scheduleRepeatingTask", new IntegerFunction() {
			@Override
			public Integer run(final FunctionParameters parameters) {
				long delay = parameters.getInt(1);
				int timerID = scheduler.startSyncRepeatingTask(new Runnable() {
					@Override
					public void run() {
						globals.get(parameters.getString(0)).call();
					}
				}, delay, delay);

				timers.add(timerID);

				return timerID;
			}
		});

		lib.set("cancelTask", new VoidFunction() {
			@Override
			protected void run(FunctionParameters parameters) {
				scheduler.cancelTask(parameters.getInt(0));
			}
		});

		return lib;
	}

	public static void disableTimers()
	{
		for (int timerID : timers)
			scheduler.cancelTask(timerID);

		timers.clear();
	}

	private static List<Integer> timers = new ArrayList<Integer>();
	private static IScheduler scheduler;
}
