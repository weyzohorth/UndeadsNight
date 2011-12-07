package me.weyzohorth.UndeadsNight;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Print
{
	static public final String WORLD = "%world%";
	static public final String INWORLD = "%inworld%";
	static public final String VALUE = "%value%";
	static public final String CAN = "%can%";
	static public final String CANNOT = "%cannot%";
	static public final String ENABLED = "%enabled%";
	static public final String DISABLED = "%disabled%";
	static public final String STATE = "%state%";
	static public final String SEP = "%separator%";
	static public Server server;
	static public Logger log;
	public String	msg;
	
	Print()
	{
	}
	
	Print(Server server, Logger log)
	{
		Print.server = server;
		Print.log = log;
	}
	
	Print(Logger log)
	{
		Print.log = log;
	}
	
	Print(Server server)
	{
		Print.server = server;
	}
	
	Print(String pattern, String world, IType value)
	{
		set(pattern, world, value);
	}
	
	public void set(String pattern, String world, IType value)
	{
		List<String> splitters = new ArrayList<String>();
		splitters.add(CAN);
		splitters.add(CANNOT);
		splitters.add(ENABLED);
		splitters.add(DISABLED);
		splitters.add(STATE);
		
		pattern = replace(pattern, WORLD, world);
		pattern = replace(pattern, INWORLD, world, "in \"", "\"");
		pattern = replaceValue(pattern, value);
		for (String s: splitters)
			if (0 <= pattern.indexOf(s))
				pattern = replaceBoolean(pattern, s, value);
		pattern = replaceSeparator(pattern, value);
		msg = pattern;
	}
	
	private String replace(String pattern, String splitter, String value)
	{
		return replace(pattern, splitter, value, "", "");
	}
	
	private String replace(String pattern, String splitter, String value, String begin_by, String begin_end)
	{
		int i = 0;
		String[] split = pattern.split(splitter);
		String ret = split[0];
		
		if (split.length == 1)
		{
			if (0 <= pattern.indexOf(splitter))
				ret += begin_by + value + begin_end;
		}
		else
			while (i + 1 < split.length)
			{
				ret += begin_by + value + begin_end + split[i + 1];
				i += 2;
			}
		return ret;
	}
	
	public void sendToAll(Server serv)
	{
		log(msg);
		serv.broadcastMessage(msg);
	}
	
	public void sendToAll()
	{
		log(msg);
		server.broadcastMessage(msg);
	}
	
	public void sendToSender(CommandSender sender)
	{
		try
		{
			((Player)sender).getName();
			log(msg);
		}
		catch (Exception e)
		{
		}
		sender.sendMessage(msg);
	}
	
	static public void log(Logger log, CommandSender sender, Command cmd, String[] args)
	{
		try
		{
			String msg = ((Player)sender).getName() + " ";
			msg += cmd.getName();
			for (String a: args)
				msg += " " + a;
			log.info(msg);
		}
		catch (Exception e)
		{
		}
	}
	
	static public void log(CommandSender sender, Command cmd, String[] args)
	{
		log(log, sender, cmd, args);
	}
	
	static public void log(String msg)
	{
		log.info(msg);
	}
	
	public String replaceValue(String pattern, IType value)
	{
		class Link
		{
			Type.etype	type;
			IGet	getter;
			Link(Type.etype t, IGet g)
			{
				type = t;
				getter = g;
			}
		}
		List<Link> list = new ArrayList<Link>();
		
		list.add(new Link(Type.etype.INT, new GetInt()));
		list.add(new Link(Type.etype.LONG, new GetLong()));
		list.add(new Link(Type.etype.DOUBLE, new GetDouble()));
		list.add(new Link(Type.etype.BOOL, new GetBoolean()));
		for (Link l: list)
			if (l.type == ((Type<?>)value).type())
				return replace(pattern, VALUE, l.getter.get(value));
		return pattern;
	}
	
	private String replaceBoolean(String pattern, String splitter, IType value)
	{
		if (((Type<?>)value).type() != Type.etype.BOOL)
			return pattern;
		class Link
		{
			String splitter;
			String on;
			String off;
			Link(String s, String on, String off)
			{
				splitter = s;
				this.on = on;
				this.off = off;
			}
		}
		List<Link> list = new ArrayList<Link>();
		
		list.add(new Link(CAN, "can", "cannot"));
		list.add(new Link(CANNOT, "cannot", "can"));
		list.add(new Link(ENABLED, "enabled", "disabled"));
		list.add(new Link(DISABLED, "disabled", "enabled"));
		list.add(new Link(STATE, "on", "off"));
		for (Link l: list)
			if (l.splitter == splitter)
			{
				if ((Boolean)value.get())
					return replace(pattern, splitter, l.on);
				return replace(pattern, splitter, l.off);
			}
		return pattern;
	}

	private String replaceSeparator(String pattern, IType value)
	{
		String[] split = pattern.split(SEP);
		String ret = "";
		int index;
		
		try
		{
			index = (Integer)value.get();
		}
		catch (Exception e)
		{
			if ((Boolean)value.get())
				index = 1;
			else
				index = 0;
		}
		if (split.length != 1)
			ret = split[index];
		else
			ret = split[0];
		return ret;
	}
	
	interface IGet
	{
		public String get(IType value);
	}
	
	class GetInt implements IGet
	{
		public String get(IType value)
		{
			return ((Integer)value.get()).toString();
		}
	}
	
	class GetLong implements IGet
	{
		public String get(IType value)
		{
			return ((Long)value.get()).toString();
		}
	}
	
	class GetDouble implements IGet
	{
		public String get(IType value)
		{
			return ((Double)value.get()).toString();
		}
	}
	
	class GetBoolean implements IGet
	{
		public String get(IType value)
		{
			return ((Boolean)value.get()).toString();
		}
	}
}
