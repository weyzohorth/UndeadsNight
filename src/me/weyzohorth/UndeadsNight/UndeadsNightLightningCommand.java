package me.weyzohorth.UndeadsNight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UndeadsNightLightningCommand extends UndeadsNightACommand
{
	UndeadsNightLightningCommand(UndeadsNight plugin)
	{
		super(plugin);
	}
	
	public boolean execute(CommandSender sender, Command cmd, String[] args)
	{
		UndeadsNightLightningStrikeListener lslistener = plugin.getLSListener();
		
		if (args.length < 1)
			return command_noarg(lslistener, sender);
		if (args[0].equalsIgnoreCase("help"))
			return command_help(sender);
		if (args[0].equalsIgnoreCase("on"))
			return command_onoff(lslistener, sender, true, ChatColor.RED + "Undeads wait the right time to strike !");
		if (args[0].equalsIgnoreCase("off"))
			return command_onoff(lslistener, sender, false, ChatColor.GRAY + "Undeads are tired of waiting, they get their cavern !");
		if (args[0].equalsIgnoreCase("strikeMe"))
			return command_strikeme(lslistener, sender);
		
		List<UndeadsNightACommand.Link>	list = new ArrayList<UndeadsNightACommand.Link>();
		list.add(new Link("durationMin", UndeadsNightLightningStrikeListener.duration_min,
				"The minimal duration of Undeads Night " + Print.INWORLD + " is " + Print.VALUE));
		
		list.add(new Link("durationMax", UndeadsNightLightningStrikeListener.duration_max,
				"The maximal duration of Undeads Night " + Print.INWORLD + " is " + Print.VALUE));
		
		list.add(new Link("start", UndeadsNightLightningStrikeListener.time_start,
				"Trying to enable the Undeads Night " + Print.INWORLD + " from " + Print.VALUE));
		
		list.add(new Link("end", UndeadsNightLightningStrikeListener.time_end,
				"Trying to enable the Undeads Night " + Print.INWORLD + " to " + Print.VALUE));
		
		list.add(new Link("explosionPower", UndeadsNightLightningStrikeListener.explosion_power,
				"Lightning explosion power " + Print.INWORLD + " is " + Print.VALUE));
		
		list.add(new Link("explosionFire", UndeadsNightLightningStrikeListener.explosion_fire,
				"Lightning explosion fire " + Print.INWORLD + " is " + Print.ENABLED));
		
		list.add(new Link("fraction", UndeadsNightLightningStrikeListener.percentage,
				"The fraction of chance for the Undeads Night to begin " + Print.INWORLD + " is 1/" + Print.VALUE));
			
		for (CreatureType type: UndeadsNightConf.allcreatures_list)
			list.add(new Link(type.toString(), UndeadsNightLightningStrikeListener.spawnBy.get(type),
					"Lightning " + type.toString() + " spawn by " + Print.VALUE + " " + Print.INWORLD));
			
		if (execute(sender, cmd, args, list))
			for (World w: plugin.getServer().getWorlds())
				plugin.getConf().saveLightning(w.getName());
		return true;
	}
	
	public boolean command_noarg(UndeadsNightLightningStrikeListener lslistener, CommandSender sender)
	{
		String msg = ChatColor.RED + "Lightning is " +
		Print.ENABLED + " " + Print.INWORLD + " (/lightning help - for some help)";
		Print printer = new Print();
	
		try
		{
			Player player = (Player)sender;
			printer.set(msg, player.getWorld().getName(), UndeadsNightLightningStrikeListener.isEnabled.get(player.getWorld().getName()));
			if ((Boolean)UndeadsNightLightningStrikeListener.isEnabled.get(player.getWorld().getName()).get())
				printer.msg = ChatColor.RED + printer.msg;
			else
				printer.msg = ChatColor.GRAY + printer.msg;
			printer.sendToSender(sender);
		}
		catch (Exception e)
		{
			for (World w: plugin.getServer().getWorlds())
			{
				printer.set(msg, w.getName(), UndeadsNightLightningStrikeListener.isEnabled.get(w.getName()));
				if ((Boolean)UndeadsNightLightningStrikeListener.isEnabled.get(w.getName()).get())
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
		sender.sendMessage("/lightning [on/off] - Allow to try to begin the UndeadsNight");
		sender.sendMessage("/lightning CreatureType [number] - Get/set the number of CreatureType which spawn when lightning strike the ground");
		sender.sendMessage("/lightning durationMin [number] - Get/set the minimal duration of the Undeads Night");
		sender.sendMessage("/lightning durationMax [number] - Get/set the maximal duration of the Undeads Night");
		sender.sendMessage("/lightning actualDuration [number] - Get/set the actual duration of the Undeads Night");
		sender.sendMessage("/lightning start [number] - Get/set the time when it tries to begin the Undeads Night");
		sender.sendMessage("/lightning end [number] - Get/set the time when it stops to try to begin the Undeads Night");
		sender.sendMessage("/lightning explosionPower [number] - Get/set the explosion power of the lightning.");
		sender.sendMessage("/lightning explosionFire [on/off] - Get/set the probability to start a fire with the lightning explosion.");
		sender.sendMessage("/lightning fraction [number] - Get/set the fraction of chance to start the Undeads Night. (1/fraction)");
		return true;
	}
	
	public boolean command_onoff(UndeadsNightLightningStrikeListener lslistener, CommandSender sender, boolean state, String msg)
	{
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			UndeadsNightLightningStrikeListener.isEnabled.get(player.getWorld().getName()).set(state);
			for (Player p: player.getWorld().getPlayers())
				p.sendMessage(msg);
			Print.log(msg);
		}
		else
		{
			for (World w: plugin.getServer().getWorlds())
				UndeadsNightLightningStrikeListener.isEnabled.get(w.getName()).set(state);
			Print.log(msg);
			plugin.getServer().broadcastMessage(msg);
		}
		return true;
	}
	
	public boolean command_strikeme(UndeadsNightLightningStrikeListener lslistener, CommandSender sender)
	{
		try
		{
			Player player = (Player)sender;
			player.getWorld().setTime(12500);
			plugin.getLSListener().startUndeadsNight(player.getWorld());
			player.getWorld().strikeLightning(player.getLocation());
		}
		catch (Exception e)
		{
			sender.sendMessage("Only players can use this command.");
		}
		return true;
	}
}
