package me.weyzohorth.UndeadsNight;

import java.util.List;
import java.util.ArrayList;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class UndeadsNightCommand
{
	public UndeadsNight plugin;
	private List<String> commandNames;
	private List<UndeadsNightACommand> commandFunctions;
	
	UndeadsNightCommand(UndeadsNight plugin)
	{
		this.plugin = plugin;
		commandNames = new ArrayList<String>();
		commandFunctions = new ArrayList<UndeadsNightACommand>();
		commandNames.add("UNSpawn");
		commandFunctions.add(new UndeadsNightSpawnCommand(plugin));
		commandNames.add("UNLightning");
		commandFunctions.add(new UndeadsNightLightningCommand(plugin));
		commandNames.add("UNBurning");
		commandFunctions.add(new UndeadsBurningCommand(plugin));
	}
	
	public boolean execute(CommandSender sender, Command cmd, String[] args)
	{
		int i = 0;
		while (i < commandNames.size())
		{
			if (cmd.getName().equalsIgnoreCase(commandNames.get(i)))
				return commandFunctions.get(i).execute(sender, cmd, args);
			i++;
		}
		return false;
	}
}
