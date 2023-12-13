package no.runsafe.eventengine.engine.events;

import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.ITimer;
import no.runsafe.framework.api.event.CancellableEvent;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncHookInvoker<TEvent> implements Runnable
{
	protected AsyncHookInvoker(TEvent event, IScheduler scheduler)
	{
		this.event = event;
		this.scheduler = scheduler;
	}

	/**
	 * Create a task to schedule execution of lua hooks as soon as possible
	 * @param hooks Potentially matching hooks to be executed
	 */
	public void sendToBackground(List<Hook> hooks)
	{
		sendToBackground(hooks, 0);
	}

	/**
	 * Create a task to schedule execution of lua hooks after the specified number of seconds have passed
	 * @param hooks Potentially matching hooks to be executed
	 * @param seconds Number of seconds to wait before trying to schedule hook executions
	 */
	public void sendToBackground(List<Hook> hooks, int seconds)
	{
		boolean ignored = true;
		this.hooks = new ArrayList<>(hooks.size());
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0, hooksSize = hooks.size(); i < hooksSize; i++)
		{
			Hook hook = hooks.get(i);
			if (filter(hook))
			{
				this.hooks.add(hook);
			}
		}
		if (this.hooks.isEmpty())
		{
			return;
		}
		scheduler.startAsyncTask(this, seconds);
	}

	/**
	 * This method will schedule execution of matching hooks
	 */
	@Override
	public void run()
	{
		// Check if event is cancelled for each step, to abort early when possible
		CancellableEvent cancellableEvent = null;
		if (event instanceof CancellableEvent)
		{
			cancellableEvent = (CancellableEvent)event;
		}
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0, hooksSize = hooks.size(); i < hooksSize; i++)
		{
			if (cancellableEvent != null && cancellableEvent.isCancelled())
			{
				return;
			}
			Hook hook = hooks.get(i);
			LuaTable table = getParameters();
			if (cancellableEvent != null && cancellableEvent.isCancelled())
			{
				return;
			}
			// wait 4 ticks to make sure any events that will be cancelled have been cancelled
			// TODO consider making this value configurable
			ITimer timer = scheduler.createSyncTimer(hook.getExecutor(table), 4L);
			if (cancellableEvent != null)
			{
				cancellableEvent.addCancellationHandle(timer::cancel);
			}
		}
	}

	/**
	 * This method is used to filter out which hooks should be invoked
	 * @param hook The hook to consider for invocation
	 * @return true to invoke the hook
	 */
	protected abstract boolean filter(Hook hook);

	/**
	 * This method is used to format the arguments for the lua invocation
	 * @return Lua function arguments
	 */
	protected abstract LuaTable getParameters();

	protected final TEvent event;
	private final IScheduler scheduler;
	private List<Hook> hooks;
}
