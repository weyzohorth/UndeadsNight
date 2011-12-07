package me.weyzohorth.UndeadsNight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

public class UndeadsNightSpawnCommand extends UndeadsNightACommand
{
	UndeadsNightSpawnCommand(UndeadsNight plugin)
	{
		super(plugin);
	}
	
	public boolean execute(CommandSender sender, Command cmd, String[] args)
	{
		UndeadsNightSpawnListener slistener = plugin.getSListener();
		
		if (args.length < 1)
			return command_noarg(slistener, sender);
		if (args[0].equalsIgnoreCase("help"))
			return command_help(sender);
		if (args[0].equalsIgnoreCase("on"))
			return command_onoff(slistener, sender, true, ChatColor.RED + "Undeads are gathering !");
		if (args[0].equalsIgnoreCase("off"))
			return command_onoff(slistener, sender, false, ChatColor.GRAY + "Undeads return to their dens !");
		
		List<UndeadsNightACommand.Link>	list = new ArrayList<UndeadsNightACommand.Link>();
		
		list.add(new Link("randomTarget", UndeadsNightSpawnListener.randomTarget,
				ChatColor.GRAY + "Undeads have enough of this empty players' brains !" + Print.SEP +
				ChatColor.YELLOW + "Undeads want players' brains !"));
		
		for (CreatureType type: UndeadsNightConf.allcreatures_list)
			list.add(new Link(type.toString(), UndeadsNightSpawnListener.spawnBy.get(type),
					type.toString() + " spawn by " + Print.VALUE + " " + Print.INWORLD));
	
		if (execute(sender, cmd, args, list))
			for (World w: plugin.getServer().getWorlds())
				plugin.getConf().saveLightning(w.getName());
		return true;
	}
	
	public boolean command_noarg(UndeadsNightSpawnListener slistener, CommandSender sender)
	{
		String msg = "Spawn is " + Print.ENABLED + " " + Print.INWORLD +
			" (/spawn help - for some help)";
		Print printer = new Print();
	
		try
		{
			Player player = (Player)sender;
			printer.set(msg, player.getWorld().getName(), UndeadsNightSpawnListener.isEnabled.get(player.getWorld().getName()));
			if ((Boolean)UndeadsNightSpawnListener.isEnabled.get(player.getWorld().getName()).get())
				printer.msg = ChatColor.RED + printer.msg;
			else
				printer.msg = ChatColor.GRAY + printer.msg;
			printer.sendToSender(sender);
		}
		catch (Exception e)
		{
			for (World w: plugin.getServer().getWorlds())
			{
				printer.set(msg, w.getName(), UndeadsNightSpawnListener.isEnabled.get(w.getName()));
				if ((Boolean)UndeadsNightSpawnListener.isEnabled.get(w.getName()).get())
					printer.msg = ChatColor.RED + printer.msg;
				else
					printer.msg = ChatColor.GRAY + printer.msg;
				printer.sendToSender(sender);
			}
		}
		return true;
	}
	
	public boolean command_help(CommandSender sender)
	{
		sender.sendMessage("/spawn [on/off] - Show/Allow creatures to spawn by groups");
		sender.sendMessage("/spawn CreatureType [number] - Get/set the number of CreatureType which spawn is the same time");
		sender.sendMessage("/spawn randomTarget [on/off] - Set a default target (a random player) to the new spawned mobs");
		return true;
	}
	
	public boolean command_onoff(UndeadsNightSpawnListener slistener, CommandSender sender, boolean state, String msg)
	{
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			UndeadsNightSpawnListener.isEnabled.get(player.getWorld().getName()).set(state);
			for (Player p: player.getWorld().getPlayers())
				p.sendMessage(msg);
			Print.log(msg);
		}
		else
		{
			for (World w: plugin.getServer().getWorlds())
				UndeadsNightSpawnListener.isEnabled.get(w.getName()).set(state);
			Print.log(msg);
			plugin.getServer().broadcastMessage(msg);
		}
		return true;
	}
}
