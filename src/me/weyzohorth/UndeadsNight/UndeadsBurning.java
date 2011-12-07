package me.weyzohorth.UndeadsNight;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class UndeadsBurning
{
	static public class DefaultConfig
	{
		static public Integer	nightFrom = 12000;
		static public Integer	nightTo = 24000;
		static public Integer	timeBurning = 1;
		static public Double	glassOpacity = 20.0;
		static public Double	leavesOpacity = 50.0;
		static public Boolean	creatureBurning = true;
		static public Boolean	creeperExplosionFire = false;
		static public Double	creeperExplosionSize = 3.0;
	}
	static private Server				server;
	private LivingEntity				entity;
	private String						world_name;
	static private boolean				isInit = false;
	static public Map<String, IType> 	night_from = new HashMap<String, IType>();
	static public Map<String, IType> 	night_to = new HashMap<String, IType>();
	static public Map<String, IType> 	time_burning = new  HashMap<String, IType>();
	static public Map<String, IType>	glass_opacity = new HashMap<String, IType>();
	static public Map<String, IType>	leaves_opacity = new HashMap<String, IType>();
	static public Map<String, IType> 	creeper_explosion_size = new HashMap<String, IType>();
	static public Map<String, IType> 	creeper_explosion_fire = new HashMap<String, IType>();
	static public UndeadsNightCreaturesMap<Boolean>	burn;

	UndeadsBurning(UndeadsNight p)
	{
		server = p.getServer();
		initMap();
	}

	UndeadsBurning(LivingEntity e)
	{
		entity = e;
		server = e.getServer();
		world_name = e.getWorld().getName();
		initMap();
	}
	
	public void initMap()
	{
		if (isInit)
			return ;
		isInit = true;
		burn = new UndeadsNightCreaturesMap<Boolean>(server, DefaultConfig.creatureBurning, "burning");
		for (World w: server.getWorlds())
		{
			night_from.put(w.getName(), new Type<Integer>(DefaultConfig.nightFrom));
			night_to.put(w.getName(), new Type<Integer>(DefaultConfig.nightTo));
			creeper_explosion_size.put(w.getName(), new Type<Double>(DefaultConfig.creeperExplosionSize));
			creeper_explosion_fire.put(w.getName(), new Type<Boolean>(DefaultConfig.creeperExplosionFire));
			time_burning.put(w.getName(), new Type<Integer>(DefaultConfig.timeBurning));
			glass_opacity.put(w.getName(), new Type<Double>(DefaultConfig.glassOpacity));
			leaves_opacity.put(w.getName(), new Type<Double>(DefaultConfig.leavesOpacity));
		}
	}
	
	public void burning()
	{
		entity.getServer().getPluginManager().callEvent(new EntityCombustEvent(entity));
		entity.setFireTicks((Integer)time_burning.get(world_name).get() * 20);
	}
	
	public boolean isBurning()
	{
		World world = entity.getWorld();
		if (world.getEnvironment() != Environment.NETHER &&
				isSunny() && isNight() == false &&
				inWater() == false && isUnderTheSun())
			return true;
		return false;
	}
	
	public boolean isNight()
	{
		long time = entity.getWorld().getTime();
		if ((Integer)night_from.get(world_name).get() <= time && time < (Integer)night_to.get(world_name).get())
			return true;
		return false;
	}
	
	public boolean isSunny()
	{
		World world = entity.getWorld();
		if (world.hasStorm() || world.isThundering())
			return false;
		return true;
	}
	
	public boolean isUnderTheSun()
	{
		Material	material;
		double		opacity = 0;
		Location	loc = entity.getLocation();
		
		int mapHeight = entity.getWorld().getHighestBlockYAt(loc);
		
		if (mapHeight <= loc.getY())
			return true;
		Block block = entity.getWorld().getHighestBlockAt(loc);
		material = block.getType();
		while (loc.getY() < block.getY() && opacity < 100)
		{
			if (material == Material.GLASS)
				opacity += (Double)glass_opacity.get(world_name).get();
			if (material == Material.LEAVES)
				opacity += (Double)leaves_opacity.get(world_name).get();
			if (material != Material.TORCH && material != Material.LADDER &&
					material != Material.PAINTING &&
					material != Material.REDSTONE_TORCH_OFF && material != Material.REDSTONE_TORCH_ON)
				return false;
			block = block.getRelative(BlockFace.DOWN);
		}
		if (opacity < 100)
			return true;
		return false;
	}
	
	public boolean inWater()
	{
		Material material = entity.getLocation().getBlock().getType();
		
		 if (material == Material.STATIONARY_WATER || material == Material.WATER)
			 return true;
		 return false;
	}
	
	public boolean tryBurning()
	{
		if (! burn.containsKey(Enum.getCreatureType(entity)) ||
			! burn.get(Enum.getCreatureType(entity), world_name))
				return false;
		burning();
		return true;
	}
}
