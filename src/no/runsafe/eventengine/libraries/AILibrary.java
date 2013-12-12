package no.runsafe.eventengine.libraries;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.lua.FunctionParameters;
import no.runsafe.framework.api.lua.IntegerFunction;
import no.runsafe.framework.api.lua.Library;
import no.runsafe.framework.api.lua.VoidFunction;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerFakeChatEvent;
import no.runsafe.framework.minecraft.player.RunsafeFakePlayer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

public class AILibrary extends Library
{
	/*
		#LUADOC
		@library AI
	 */
	public AILibrary(RunsafePlugin plugin, IOutput output)
	{
		super(plugin, "ai");
		AILibrary.output = output;
	}

	@Override
	protected LuaTable getAPI()
	{
		LuaTable lib = new LuaTable();
		lib.set("create", new IntegerFunction()
		{
			@Override
			public Integer run(FunctionParameters parameters)
			{
				return createAI(parameters.getString(0), parameters.getString(1), parameters.getWorld(2));
			}
		});
		lib.set("speak", new VoidFunction()
		{
			@Override
			protected void run(FunctionParameters parameters)
			{
				speak(parameters.getInt(0), parameters.getString(1));
			}
		});
		return lib;
	}

	/*
		#LUADOC
		@function speak Allows you to broadcast a message as an AI.
		@param Integer id The ID of the AI, provided by the create function.
		@param String message The message to be broadcast by the AI.
	 */
	private static void speak(int id, String message)
	{
		if (ai.size() <= id)
			throw new LuaError("No AI with given ID.");

		RunsafePlayerFakeChatEvent.Broadcast(AILibrary.ai.get(id), message, output);
	}

	/*
		#LUADOC
		@function create Creates an AI and returns the ID for use with AI functions.
		@param String name The name of the AI, will be shown in chat.
		@param String group A user-group for the AI.
		@param IWorld world The world in which the AI will appear to be in.
		@return Integer The ID of the AI for use with AI functions.
	 */
	private static int createAI(String name, String group, IWorld world)
	{
		RunsafeFakePlayer newAI = new RunsafeFakePlayer(name, group);
		newAI.setWorld(world);

		AILibrary.ai.add(newAI);
		return AILibrary.ai.size() - 1;
	}

	private static IOutput output;
	private static final List<RunsafeFakePlayer> ai = new ArrayList<RunsafeFakePlayer>();
}
