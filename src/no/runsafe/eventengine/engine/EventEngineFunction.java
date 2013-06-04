package no.runsafe.eventengine.engine;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.List;

public class EventEngineFunction extends VarArgFunction
{
	public Varargs invoke(Varargs args)
	{
		FunctionParameters parameters = new FunctionParameters();
		int currentIndex = 1;
		while (!args.isnoneornil(currentIndex))
		{
			parameters.addParameter(args.checkvalue(currentIndex));
			currentIndex += 1;
		}

		this.run(parameters);
		return null;
	}

	public List<Object> run(FunctionParameters parameters)
	{
		return null;
	}
}
