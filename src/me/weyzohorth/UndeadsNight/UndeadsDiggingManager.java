package me.weyzohorth.UndeadsNight;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class UndeadsDiggingManager implements Runnable
{
	private class DiggingItem
	{
		public Entity	entity;
		public Block	block;
		public long		time;
		public Location	loc;
		
		DiggingItem(Entity e, Block b)
		{
			entity = e;
			block = b;
			loc = entity.getLocation().getBlock().getLocation();
			time = loc.getWorld().getFullTime() + UndeadsDiggingTimerConf.getTime(entity.getWorld().getName(), block.getType());
		}
	}
	
	static private UndeadsNight	plugin;
	static private List<UndeadsDiggingManager.DiggingItem> list = new ArrayList<UndeadsDiggingManager.DiggingItem>();
	
	UndeadsDiggingManager(UndeadsNight plug)
	{
		plugin = plug;
	}
	
	static public boolean breakBlock(Entity entity, Block block)
	{
		if (40 < list.size())
			return false;
		if (block.getType() == Material.AIR ||
				block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ||
				block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
			return true;
		if (UndeadsDiggingTimerConf.getTime(entity.getWorld().getName(), block.getType()) < 0)
			return false;
		for (DiggingItem items: list)
			if (items.entity == entity)
				return false;
		if (UndeadsDiggingTimerConf.getTime(entity.getWorld().getName(), block.getType()) == 0)
		{
			UndeadsDigging.breakBlock(block, entity);
			return true;
		}
		list.add(plugin.getDiggingManager().new DiggingItem(entity, block));
		return true;
	}
	
	public void run()
	{
		DiggingItem item;
		for (int i = 0; i < list.size(); i++)
		{
			item = list.get(i);
			Location loc = item.entity.getLocation().getBlock().getLocation();
			if (item.entity.isDead() ||
					loc.getX() != item.loc.getX() || loc.getZ() != item.loc.getZ())
			{
				list.remove(item);
				continue;
			}
			if (item.time <= item.loc.getWorld().getFullTime())
				continue;
			UndeadsDigging.breakBlock(item.block, item.entity);
			list.remove(item);
		}
	}
}
