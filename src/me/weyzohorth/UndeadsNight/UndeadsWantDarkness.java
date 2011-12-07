package me.weyzohorth.UndeadsNight;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.block.Block;

public class UndeadsWantDarkness
{
	static private UndeadsNight			plugin;
	static boolean						isInit = false;
	static public class DefaultConfig
	{
		static public Integer	creatureRadius = 3;
		static public Integer	creatureHeight = 3;
		static public Boolean	creatureWantsDarkness = true;
	}
	static public UndeadsNightCreaturesMap<Integer>	radius;
	static public UndeadsNightCreaturesMap<Integer>	height;
	static public UndeadsNightCreaturesMap<Boolean>	wantsDarkness;
	
	UndeadsWantDarkness(UndeadsNight plug)
	{
		plugin = plug;
		initMap();
	}
	
	void initMap()
	{
		if (isInit)
			return ;
		radius = new UndeadsNightCreaturesMap<Integer>(plugin.getServer(), DefaultConfig.creatureRadius, "radius");
		height = new UndeadsNightCreaturesMap<Integer>(plugin.getServer(), DefaultConfig.creatureHeight, "height");
		wantsDarkness = new UndeadsNightCreaturesMap<Boolean>(plugin.getServer(), DefaultConfig.creatureWantsDarkness, "darkness");
		isInit = true;
	}
	
	public boolean check_is_solid(Material type)
	{
		if (type != Material.AIR &&
				type != Material.WATER && type != Material.STATIONARY_WATER &&
				type != Material.LAVA && type != Material.STATIONARY_LAVA &&
				type != Material.LADDER && type != Material.PAINTING &&
				type != Material.RAILS &&
				type != Material.REDSTONE_TORCH_OFF && type != Material.REDSTONE_TORCH_ON &&
				type != Material.REDSTONE_WIRE &&
				type != Material.WOOD_PLATE && type != Material.STONE_PLATE &&
				type != Material.STONE_BUTTON && type != Material.SIGN_POST)
			return true;
		return false;
	}
	
	public int get_torch(Entity entity, Location loc)
	{
		for (int y = 0; y <= height.get(entity); y++)
		{
			Material type = loc.getBlock().getRelative(0, y, 0).getType();
			if (type == Material.TORCH)
				return y;
			else if (check_is_solid(type))
				return -1;
		}
		return -1;
	}
	
	public void darkness(Entity entity)
	{
		Block block = entity.getLocation().getBlock();
		if (wantsDarkness.containsKey(Enum.getCreatureType(entity), entity.getWorld().getName()) == false ||
				wantsDarkness.get(Enum.getCreatureType(entity), entity.getWorld().getName()) == false)
			return ;
		int y = get_torch(entity, entity.getLocation());
		if (y != -1)
			UndeadsDigging.breakBlock(block.getRelative(0, y, 0), entity);
		for (int x = 1; x <= radius.get(entity); x++)
			for (int z = 1; z <= radius.get(entity); z++)
			{
				
				for (int mask = 0; mask <= 3; mask++)
				{
					int sign_x = x;
					int sign_z = z;
					if ((mask & 1) == 1)
						sign_x *= -1;
					if ((mask & 2) == 1)
						sign_z *= -1;
					y = 0;
					while (y != -1)
					{
						y = get_torch(entity, block.getRelative(sign_x, 0, sign_z).getLocation());
						if (y != -1)
						{
							UndeadsDigging.breakBlock(block.getRelative(sign_x, y, sign_z), entity);
							return;
						}
					}
				}
			}
		
	}
}
