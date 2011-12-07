package me.weyzohorth.UndeadsNight;

import java.util.Map;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class UndeadsNightACommand
{
	class Link
	{
		String	cmd;
		Map<String, IType> value;
		String	msg;
		boolean all;
		Link(String c, Map<String, IType> v, String m, boolean a)
		{
			cmd = c;
			value = v;
			msg = m;
			all = a;
		}
		
		Link(String c, Map<String, IType> v, String m)
		{
			cmd = c;
			value = v;
			msg = m;
			all = false;
		}
	}
	static public final String ALL = "*";
	static public final String NONE = "";
	static public final String IN = "in";
	static public final String ON = "on";
	static public final String OFF = "off";
	static public final String SET = "set";
	static public final String SEP = "|";
	private final Print printer = new Print();
	public UndeadsNight plugin;
	
	UndeadsNightACommand()
	{	
	}
	
	UndeadsNightACommand(UndeadsNight plugin)
	{
		this.plugin = plugin;
	}
	
	abstract public boolean execute(CommandSender sender, Command cmd, String[] args);
	
	public boolean execute(CommandSender sender, Command cmd, String[] args, List<Link> cmds)
	{
		boolean ret = false;
		
		try
		{
			for (Link l: cmds)
			{
				if (args[0].equalsIgnoreCase(l.cmd))
				{
					Print.log(sender, cmd, args);
					ret = setAttr(sender, args, l.value);
					getAttr(sender, args, l.value, l.all, l.msg);
					break;
				}
			}
			return ret;
		}
		catch (Exception e)
		{
			sender.sendMessage(cmd.getUsage());
			return false;
		}
	}
	
	public boolean setAttr(CommandSender sender, String[] args, Map<String, IType> attr)
	{
		String world = getWorld(args);
		int index = 3;
		if (world == NONE)
		{
			index = 1;
			world = getWorld(sender);
		}
		if (world != ALL)
			return set(args, attr.get(world), index);
		else
			for (World w: plugin.getServer().getWorlds())
				if (! set(args, attr.get(w.getName()), index))
					return false;
		return true;
	}
	
	public void getAttr(CommandSender sender, String[] args, Map<String, IType> attr, boolean dispAll, String msg)
	{
		String world = getWorld(args);
		if (world == NONE)
			world = getWorld(sender);
		if (world != ALL)
		{
			printer.set(msg, world, attr.get(world));
			send(sender, dispAll);
		}
		else
			for (World w: plugin.getServer().getWorlds())
			{
				printer.set(msg, w.getName(), attr.get(w.getName()));
				send(sender, dispAll);
			}
	}
	
	public void send(CommandSender sender, boolean dispAll)
	{
		if (dispAll)
			printer.sendToAll();
		else
			printer.sendToSender(sender);
	}
	
	public String getWorld(String[] args)
	{
		if (args.length <= 3 || ! args[1].equalsIgnoreCase(IN))
			return NONE;
		if (args[2].equalsIgnoreCase("all"))
			return ALL;
		for (World w : plugin.getServer().getWorlds())
			if (w.getName().equalsIgnoreCase(args[2]))
				return w.getName();
		return NONE;
	}
	
	public String getWorld(CommandSender sender)
	{
		try
		{
			return ((Player)sender).getWorld().getName();
		}
		catch (Exception e)
		{
			return ALL;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean set(String[] args, IType value, int index)
	{
		if (((Type<?>)value).type() == Type.etype.BOOL)
			return getState(args, (Type<Boolean>)value, index);
		else if (((Type<?>)value).type() == Type.etype.INT)
			return getInt(args, (Type<Integer>)value, index);
		else if (((Type<?>)value).type() == Type.etype.LONG)
			return getLong(args, (Type<Long>)value, index);
		else if (((Type<?>)value).type() == Type.etype.DOUBLE)
			return getDouble(args, (Type<Double>)value, index);
		return false;
	}
	
	public String get(IType value)
	{
		if (((Type<?>)value).type() == Type.etype.BOOL)
		{
			if (((Boolean)value.get()) == true)
				return ON;
			return OFF;
		}
		else if (((Type<?>)value).type() == Type.etype.INT)
			return Integer.toString(((Integer)value.get()));
		else if (((Type<?>)value).type() == Type.etype.LONG)
			return Long.toString(((Long)value.get()));
		else if (((Type<?>)value).type() == Type.etype.DOUBLE)
			return Double.toString(((Double)value.get()));
		return NONE;
	}
	
	public boolean getState(String[] args, Type<Boolean> value, int index)
	{
		if (args.length <= index)
			return false;
		if (args[index].equalsIgnoreCase(ON))
		{
			value.set(true);
			return true;
		}
		if (args[index].equalsIgnoreCase(OFF))
		{
			value.set(false);
			return true;
		}
		return false;
	}
	
	public boolean getInt(String[] args, Type<Integer> value, int index)
	{
		try
		{
			value.set(Integer.parseInt(args[index]));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public boolean getLong(String[] args, Type<Long> value, int index)
	{
		try
		{
			value.set(Long.parseLong(args[index]));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public boolean getDouble(String[] args, Type<Double> value, int index)
	{
		try
		{
			value.set(Double.parseDouble(args[index]));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
