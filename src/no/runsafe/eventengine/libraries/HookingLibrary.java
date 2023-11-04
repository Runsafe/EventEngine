package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.EventEngine;
import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.engine.hooks.HookType;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

import java.util.logging.Logger;

public class HookingLibrary extends Library
{
	public HookingLibrary(RunsafePlugin plugin)
	{
		super(plugin, "hooks");
		logger = plugin.getLogger();
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
			EventEngine.Debugger.debugFiner("Registering hook on thread #%d %s", Thread.currentThread().getId(),
			                                Thread.currentThread().getName()
			);
			HookType type;
			String typeArgument = parameters.getString(0);
			try
			{
				type = HookType.valueOf(typeArgument);
			}
			catch (IllegalArgumentException e)
			{
				throw new LuaError("Invalid hook type " + typeArgument);
			}
			String function = parameters.getString(1);
			Hook hook = new Hook(type, function, globals, logger);
			EventEngine.Debugger.debugFine("Registering %s hook %s", type, function);

			switch (type)
			{
				case BLOCK_GAINS_CURRENT:
					hook.setLocation(parameters.getLocation(2));
					break;
				case REGION_ENTER:
				case REGION_LEAVE:
					hook.setData(parameters.getString(2));
					break;
				case INTERACT:
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
					break;
				case PLAYER_LOGIN:
				case PLAYER_LOGOUT:
					hook.setPlayerName(parameters.getString(2));
					break;
				case BLOCK_BREAK:
				case LEFT_CLICK_BLOCK:
					if (parameters.parameterCount() > 2) hook.setWorld(parameters.getWorld(2));
					break;
				case CHAT_MESSAGE:
				case PLAYER_DAMAGE:
				case PLAYER_DEATH:
				case PLAYER_ITEM_DROP:
				case PLAYER_ITEM_PICKUP:
					hook.setWorld(parameters.getWorld(2));
					break;
			}

			HookHandler.registerHook(hook);
		}
	}

	private final Logger logger;
}
