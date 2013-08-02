package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.Plugin;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.IntegerFunction;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import no.runsafe.framework.internal.lua.Environment;
import org.luaj.vm2.LuaTable;

public class TimerLibrary extends Library
{
	protected TimerLibrary(RunsafePlugin plugin)
	{
		super(plugin, "timer");
		this.scheduler = ((Plugin) plugin).scheduler;
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();

		lib.set("scheduleTask", new IntegerFunction() {
			@Override
			public Integer run(final FunctionParameters parameters) {
				return scheduler.startSyncTask(new Runnable() {
					@Override
					public void run() {
						Environment.global.get(parameters.getString(0)).call();
					}
				}, parameters.getInt(1));
			}
		});

		lib.set("scheduleRepeatingTask", new IntegerFunction() {
			@Override
			public Integer run(final FunctionParameters parameters) {
				int delay = parameters.getInt(1);
				return scheduler.startSyncRepeatingTask(new Runnable() {
					@Override
					public void run() {
						Environment.global.get(parameters.getString(0)).call();
					}
				}, delay, delay);
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
	private IScheduler scheduler;
}
