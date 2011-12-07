package me.weyzohorth.UndeadsNight;

import java.util.List;
import java.util.ArrayList;


import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;

public class UndeadsBurningCommand extends UndeadsNightACommand
{
	UndeadsBurningCommand(UndeadsNight plugin)
	{
		super(plugin);
	}
	
	public boolean execute(CommandSender sender, Command cmd, String[] args)
	{
		if (args.length < 1)
			return command_noarg(sender, cmd);
		if (args[0].equalsIgnoreCase("help"))
			return command_help(sender);

		List<UndeadsNightACommand.Link>	list = new ArrayList<UndeadsNightACommand.Link>();
		list.add(new Link("duration", UndeadsBurning.time_burning,
				"Burning duration " + Print.INWORLD + " is " + Print.VALUE + " seconds"));
		list.add(new Link("start", UndeadsBurning.night_from,
				"Mobs begin to burn at " + Print.VALUE + " " + Print.INWORLD));
		list.add(new Link("end", UndeadsBurning.night_to,
				"Mobs end to burn at " + Print.VALUE + " " + Print.INWORLD));
		list.add(new Link("creeperExplosion", UndeadsBurning.creeper_explosion_fire,
				"Creeper explosion " + Print.CAN + " create fire " + Print.INWORLD));
		list.add(new Link("glassOpacity", UndeadsBurning.glass_opacity,
				"glass opacity " + Print.INWORLD + ": " + Print.VALUE));
		list.add(new Link("leavesOpacity", UndeadsBurning.leaves_opacity,
				"leaves opacity " + Print.INWORLD + ": " + Print.VALUE));
		list.add(new Link("leavesOpacity", UndeadsBurning.leaves_opacity,
				"leaves opacity " + Print.INWORLD + ": " + Print.VALUE));
		for (CreatureType type: UndeadsNightConf.allcreatures_list)
			list.add(new Link(type.toString(), UndeadsBurning.burn.get(type),
					type.toString() + " " + Print.INWORLD + ", " + Print.CAN + " burn in sunlight"));
		if (execute(sender, cmd, args, list))
			for (World w: plugin.getServer().getWorlds())
				plugin.getConf().saveBurning(w.getName());
		return true;
	}
	
	public boolean command_noarg(CommandSender sender, Command cmd)
	{
		sender.sendMessage(cmd.getUsage());
		return true;
	}
	
	public boolean command_help(CommandSender sender)
	{
		sender.sendMessage("/burning world duration [number]");
		sender.sendMessage("/burning world start [number]");
		sender.sendMessage("/burning world end [number]");
		sender.sendMessage("/burning world glassOpacity [number]");
		sender.sendMessage("/burning world leavesOpacity [number]");
		sender.sendMessage("/burning world creeper [on/off]");
		sender.sendMessage("/burning world ghast [on/off]");
		sender.sendMessage("/burning world giant [on/off]");
		sender.sendMessage("/burning world pigzombie [on/off]");
		sender.sendMessage("/burning world skeleton [on/off]");
		sender.sendMessage("/burning world slime [on/off]");
		sender.sendMessage("/burning world spider [on/off]");
		sender.sendMessage("/burning world zombie [on/off]");
		return true;
	}
}
