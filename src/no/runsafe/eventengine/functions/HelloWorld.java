package no.runsafe.eventengine.functions;

import no.runsafe.framework.server.RunsafeServer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class HelloWorld extends ZeroArgFunction
{
	public HelloWorld() {}

	@Override
	public LuaValue call()
	{
		RunsafeServer.Instance.broadcastMessage("Hello, world!");
		return null;
	}
}
