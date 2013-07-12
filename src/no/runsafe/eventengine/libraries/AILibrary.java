package no.runsafe.eventengine.libraries;

import no.runsafe.framework.lua.FunctionParameters;
import no.runsafe.framework.lua.RunsafeLuaFunction;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerFakeChatEvent;
import no.runsafe.framework.minecraft.player.RunsafeFakePlayer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.ArrayList;
import java.util.List;

public class AILibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("create", new Create());
		lib.set("speak", new Speak());

		env.get("api").set("ai", lib);
		return lib;
	}

	public static int createAI(String name, String group, RunsafeWorld world)
	{
		RunsafeFakePlayer newAI = new RunsafeFakePlayer(name);
		newAI.getGroups().add(group);
		newAI.setWorld(world);

		AILibrary.ai.add(newAI);
		return AILibrary.ai.size() - 1;
	}

	static class Create extends RunsafeLuaFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returnValues = new ArrayList<Object>();
			returnValues.add(AILibrary.createAI(parameters.getString(0), parameters.getString(1), parameters.getWorld(2)));
			return returnValues;
		}
	}

	static class Speak extends RunsafeLuaFunction
	{
		// aiID, text
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			int id = parameters.getInt(0);
			if (ai.size() <= id)
				throw new LuaError("No AI with given ID.");

			RunsafePlayerFakeChatEvent event = new RunsafePlayerFakeChatEvent(AILibrary.ai.get(id), parameters.getString(1));
			event.Fire();

			if (!event.getCancelled())
				RunsafeServer.Instance.broadcastMessage(String.format(event.getFormat(), event.getPlayer().getName(), event.getMessage()));

			return null;
		}
	}

	private static final List<RunsafeFakePlayer> ai = new ArrayList<RunsafeFakePlayer>();
}
