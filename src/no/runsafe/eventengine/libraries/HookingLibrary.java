package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.engine.hooks.HookType;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

import java.util.logging.Logger;

public class HookingLibrary extends Library
{
	public HookingLibrary(RunsafePlugin plugin, IDebug debugger)
	{
		super(plugin, "hooks");
		logger = plugin.getLogger();
		debug = debugger;
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("registerHook", new RegisterHook());
		return lib;
	}

	private class RegisterHook extends VoidFunction
	{
		@Override
		public void run(FunctionParameters parameters)
		{
			EventEngine.Debugger.debugFiner(
				"Setting block on thread #%d %s",
				Thread.currentThread().getId(), Thread.currentThread().getName()
			);
			HookType type;
			String typeArgument = parameters.getString(0);
			try
			{
				type = HookType.valueOf(typeArgument);
			}
			catch(IllegalArgumentException e)
			{
				throw new LuaError("Invalid hook type " + typeArgument);
			}
			String function = parameters.getString(1);
			Hook hook = new Hook(type, function, globals, logger, debug);
			EventEngine.Debugger.debugFine("Registering %s hook %s", type, function);

			if (type == HookType.BLOCK_GAINS_CURRENT)
			{
				hook.setLocation(parameters.getLocation(2));
			} else if (type == HookType.REGION_ENTER || type == HookType.REGION_LEAVE)
			{
				hook.setData(parameters.getString(2));
			} else if (type == HookType.CHAT_MESSAGE)
			{
				hook.setWorld(parameters.getWorld(2));
			} else if (type == HookType.INTERACT)
			{
				int parCount = parameters.parameterCount();
				if (parCount == 3)
				{
					hook.setWorld(parameters.getWorld(2));
				} else if (parCount == 4)
				{
					hook.setData(parameters.getInt(2));
					hook.setWorld(parameters.getWorld(3));
				} else if (parCount > 4)
				{
					hook.setData(parameters.getInt(2));
					hook.setLocation(parameters.getLocation(3));
				}
			} else if (type == HookType.PLAYER_LOGIN || type == HookType.PLAYER_LOGOUT)
			{
				hook.setPlayerName(parameters.getString(2));
			} else if (type == HookType.BLOCK_BREAK || type == HookType.LEFT_CLICK_BLOCK)
			{
				if (parameters.parameterCount() > 2)
					hook.setWorld(parameters.getWorld(2));
			} else if (type == HookType.PLAYER_DAMAGE || type == HookType.PLAYER_DEATH || type == HookType.PLAYER_ITEM_DROP || type == HookType.PLAYER_ITEM_PICKUP)
			{
				hook.setWorld(parameters.getWorld(2));
			}

			HookHandler.registerHook(hook);
		}
	}

	private final Logger logger;
	private final IDebug debug;
}
