package me.weyzohorth.UndeadsNight;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;

public class UndeadsDiggingTimerConf
{
	private UndeadsNight	plugin;
	private static Map<String, HashMap<String, Integer>>	time = new HashMap<String, HashMap<String, Integer>>();
	
	UndeadsDiggingTimerConf(UndeadsNight plug)
	{
		plugin = plug;
		initMap();
		initValues();
	}
	
	private void initMap()
	{
		for (World w: plugin.getServer().getWorlds())
				time.put(w.getName(), new HashMap<String, Integer>());
	}
	
	private void initValues()
	{
		List<List<Material>>	durations = new ArrayList<List<Material>>();
		for (int i = 0; i <= 6; i++)
			durations.add(new ArrayList<Material>());
		
		durations.get(0).add(Material.OBSIDIAN);
		durations.get(0).add(Material.BEDROCK);
		
		durations.get(1).add(Material.TORCH);
		durations.get(1).add(Material.REDSTONE_TORCH_ON);
		durations.get(1).add(Material.REDSTONE_TORCH_OFF);
		durations.get(1).add(Material.REDSTONE_WIRE);
		durations.get(1).add(Material.DIODE);
		durations.get(1).add(Material.DIODE_BLOCK_ON);
		durations.get(1).add(Material.DIODE_BLOCK_OFF);
		
		durations.get(3).add(Material.STONE);
		durations.get(3).add(Material.COBBLESTONE);
		durations.get(3).add(Material.MOSSY_COBBLESTONE);
		durations.get(3).add(Material.CHEST);
		
		durations.get(4).add(Material.IRON_ORE);
		durations.get(4).add(Material.GOLD_ORE);
		durations.get(4).add(Material.REDSTONE_ORE);
		durations.get(4).add(Material.DIAMOND_ORE);
		durations.get(4).add(Material.LAPIS_ORE);
		durations.get(4).add(Material.COAL_ORE);
		durations.get(4).add(Material.GOLD_BLOCK);
		durations.get(4).add(Material.FURNACE);
		durations.get(4).add(Material.FENCE);
		
		durations.get(5).add(Material.IRON_BLOCK);
		
		durations.get(6).add(Material.DIAMOND_BLOCK);
		durations.get(6).add(Material.LAPIS_BLOCK);
		
		for (String world: time.keySet())
			for (Material type: Material.values())
				if (type.isBlock())
					time.get(world).put(type.toString(), 1);
		for (String world: time.keySet())
		{
			int i = -1;
			for (List<Material> duration: durations)
			{
				for (Material type: duration)
					if (type.isBlock())
							time.get(world).put(type.toString(), i);
				i++;
			}
		}
	}
	
	static public boolean setTime(String world, Material type, Integer time)
	{
		if (! type.isBlock())
			return false;
		UndeadsDiggingTimerConf.time.get(world).put(type.toString(), time);
		return true;
	}
	
	static public Integer getTime(String world, Material type)
	{
		if (! type.isBlock())
			return -2;
		return UndeadsDiggingTimerConf.time.get(world).get(type.toString());
	}
	
	static public Map<String, Integer> getTime(String world)
	{
		return time.get(world);
	}
	
	static public void setTime(String world, HashMap<String, Integer> time)
	{
		UndeadsDiggingTimerConf.time.put(world, time);
	}
}
