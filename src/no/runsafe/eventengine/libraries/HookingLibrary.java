package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.engine.EventEngineFunction;
import no.runsafe.eventengine.engine.FunctionParameters;
import no.runsafe.eventengine.engine.hooks.Hook;
import no.runsafe.eventengine.engine.hooks.HookHandler;
import no.runsafe.eventengine.engine.hooks.HookType;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.List;

public class HookingLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("registerHook", new RegisterHook());

		env.get("engine").set("hooks", lib);
		return lib;
	}

	static class RegisterHook extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			HookType type = HookType.valueOf(parameters.getString(0));
			if (type == null)
				throw new LuaError("Invalid hook type");

			Hook hook = new Hook(type, parameters.getString(1));

			if (type == HookType.BLOCK_GAINS_CURRENT)
			{
				hook.setLocation(parameters.getLocation(2));
			}
			else if (type == HookType.REGION_ENTER || type == HookType.REGION_LEAVE)
			{
				hook.setPlayerName(parameters.getString(2));
				hook.setData(parameters.getString(3));
			}
			else if (type == HookType.CHAT_MESSAGE)
			{
				hook.setData(parameters.getString(2));
			}
			else if (type == HookType.INTERACT)
			{
				hook.setData(Integer.parseInt(parameters.getString(2)));
				hook.setLocation(parameters.getLocation(3));
			}
			else if (type == HookType.PLAYER_LOGIN || type == HookType.PLAYER_LOGOUT)
			{
				hook.setPlayerName(parameters.getString(2));
			}

			HookHandler.registerHook(hook);
			return null;
		}
	}
}
