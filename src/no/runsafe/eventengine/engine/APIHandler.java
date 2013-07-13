package no.runsafe.eventengine.engine;

import no.runsafe.eventengine.libraries.*;
import no.runsafe.framework.lua.LuaEnvironment;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class APIHandler extends OneArgFunction
{
	@Override
	public LuaValue call(LuaValue env)
	{
		LuaTable lib = new LuaTable();
//		env.set("api", lib);
//		env.get("package").get("loaded").set("api", lib);
//
//		LuaEnvironment.loadFile("plugins/EventEngine/lua/engine.lua");
//		LuaEnvironment.global.load(new HookingLibrary());
//		LuaEnvironment.global.load(new PlayerLibrary());
//		LuaEnvironment.global.load(new EffectLibrary());
//		LuaEnvironment.global.load(new WorldLibrary());
//		LuaEnvironment.global.load(new SoundLibrary());
//		LuaEnvironment.global.load(new AILibrary());

		return lib;
	}
}
