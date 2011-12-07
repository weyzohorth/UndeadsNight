package me.weyzohorth.UndeadsNight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.CreatureType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

public class UndeadsNightSpawnListener extends EntityListener
{
	static public class DefaultConfig
	{
		static public Boolean isEnabled	= true;
		static public Boolean randomTarget = true;
		static public Integer creatureSpawnsBy = 6;
	}
	public UndeadsNight					plugin;
	static public List<World>			worlds;
	static private boolean				isInit = false;
	static public Map<String, IType>	isEnabled = new HashMap<String, IType>();
	static public Map<String, IType>	randomTarget = new HashMap<String, IType>();
	static public UndeadsNightCreaturesMap<Integer>	spawnBy;
	
	public UndeadsNightSpawnListener(UndeadsNight plugin)
	{
		this.plugin = plugin;
		worlds = plugin.getServer().getWorlds();
		initMap();
	}
	
	private void initMap()
	{
		if (isInit)
			return ;
		spawnBy = new UndeadsNightCreaturesMap<Integer>(plugin.getServer(), DefaultConfig.creatureSpawnsBy, "spawn");
		for (World w: worlds)
		{
			isEnabled.put(w.getName(), new Type<Boolean>(DefaultConfig.isEnabled));
			randomTarget.put(w.getName(), new Type<Boolean>(DefaultConfig.randomTarget));
		}
		isInit = true;
	}
	public void spawnMob(CreatureType type, Location loc, int number)
	{
		Random rand = new Random();
		for (int i = 0; i < number; i++)
		{
			try
			{
				Monster monster = (Monster)loc.getWorld().spawnCreature(loc, type);
				if ((Boolean)randomTarget.get(loc.getWorld().getName()).get())
					monster.setTarget(plugin.getServer().getOnlinePlayers()[rand.nextInt(plugin.getServer().getOnlinePlayers().length)]);
			}
			catch (Exception e)
			{
				//not a monster
			}
		}
	}
	
	public void spawnMob(CreatureSpawnEvent c, int number)
	{
		spawnMob(c.getCreatureType(), c.getLocation(), number);
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent e)
	{
		String world = e.getEntity().getWorld().getName();
		if ((Boolean)isEnabled.get(world).get() == false || e.getSpawnReason()== SpawnReason.CUSTOM)
			return ;
		
		for (CreatureType type: UndeadsNightConf.creatures_list.get(world))
			if (type == Enum.getCreatureType(e.getEntity()))
			{
				spawnMob(e, spawnBy.get(type, world));
				break;
			}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent e)
	{
		String name = e.getEntity().getWorld().getName();

		plugin.getLSListener();
		plugin.getLSListener();
		if ((Boolean)UndeadsNightLightningStrikeListener.isEnabled.get(name).get() == false ||
				(Boolean)UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).get() == false ||
				e.getCause() != DamageCause.LIGHTNING && e.getCause() != DamageCause.BLOCK_EXPLOSION)
			return ;
		if (AIsEntity.isMonster(e.getEntity()) == false &&
				AIsEntity.isGhast(e.getEntity()) == false &&
				AIsEntity.isSlime(e.getEntity()) == false)
			return ;
		LivingEntity entity = (LivingEntity)e.getEntity();
		if (entity.getHealth() + e.getDamage() <= 200)
			entity.setHealth(entity.getHealth() + e.getDamage());
		else
			entity.setHealth(200);
	}
}
