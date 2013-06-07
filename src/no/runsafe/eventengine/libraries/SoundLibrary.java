package no.runsafe.eventengine.libraries;

import no.runsafe.eventengine.engine.EventEngineFunction;
import no.runsafe.eventengine.engine.FunctionParameters;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.moosic.MusicHandler;
import no.runsafe.moosic.MusicTrack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoundLibrary extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
		lib.set("playSong", new PlaySong());
		lib.set("stopSong", new StopSong());

		env.get("engine").set("sound", lib);
		return lib;
	}

	static class PlaySong extends EventEngineFunction
	{
		// world, x, y, z, songFile, volume
		// Returns int
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			List<Object> returnValues = new ArrayList<Object>();
			RunsafeLocation location = parameters.getLocation(0);

			File songFile = SoundLibrary.musicHandler.loadSongFile(parameters.getString(4));
			if (!songFile.exists())
				throw new LuaError("Music file not found.");

			try
			{
				returnValues.add(SoundLibrary.musicHandler.startSong(new MusicTrack(songFile), location, parameters.getFloat(5)));
			}
			catch (Exception e)
			{
				throw new LuaError(e.getMessage());
			}

			return returnValues;
		}
	}

	static class StopSong extends EventEngineFunction
	{
		@Override
		public List<Object> run(FunctionParameters parameters)
		{
			int playerID = parameters.getInt(0);
			if (SoundLibrary.musicHandler.playerExists(playerID))
				SoundLibrary.musicHandler.forceStop(playerID);

			return null;
		}
	}

	public static MusicHandler musicHandler;
}
