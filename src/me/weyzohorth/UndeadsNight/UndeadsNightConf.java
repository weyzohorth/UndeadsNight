package me.weyzohorth.UndeadsNight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import org.yaml.snakeyaml.Yaml;

public class UndeadsNightConf
{
	public UndeadsNight					plugin;
	public Logger						log = Logger.getLogger("Minecraft");
	public static String				main_dir = "plugins/UndeadsNight" + File.separator;
	public static Map<String, File>		undeadsnight_conf = new HashMap<String, File>();
	public static Map<String, File>		creatures_conf = new HashMap<String, File>();
	public static Map<String, File>		timer_conf = new HashMap<String, File>();
	public static Map<String, Set<CreatureType>>	creatures_list = new HashMap<String, Set<CreatureType>>();
	public static Set<CreatureType>	allcreatures_list = new HashSet<CreatureType>();
	public static Map<String, Map<String, UndeadsNightDefaultConf>>	creatures_map = new HashMap<String, Map<String, UndeadsNightDefaultConf>>();

	UndeadsNightConf(UndeadsNight p)
	{
		plugin = p;
		createPath();
	}
	
	public boolean addCreature(String world, String name)
	{
		CreatureType type = Enum.getCreatureType(name);
		if (type == null)
			return false;
		creatures_list.get(world).add(type);
		allcreatures_list.add(type);
		for (Class<?> creature: UndeadsNightDefaultConf.class.getDeclaredClasses())
		{
			if (creature.getSimpleName().equalsIgnoreCase(type.toString()))
			{
				try
				{
					creatures_map.get(world).put(type.toString(), (UndeadsNightDefaultConf)creature.newInstance());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
		return true;
	}
	
	public void initCreaturesList(String world)
	{
		creatures_list.put(world, new HashSet<CreatureType>());
		creatures_map.put(world, new HashMap<String, UndeadsNightDefaultConf>());
		creatures_list.get(world).add(CreatureType.CREEPER);
		creatures_list.get(world).add(CreatureType.GHAST);
		creatures_list.get(world).add(CreatureType.GIANT);
		creatures_list.get(world).add(CreatureType.PIG_ZOMBIE);
		creatures_list.get(world).add(CreatureType.SKELETON);
		creatures_list.get(world).add(CreatureType.SLIME);
		creatures_list.get(world).add(CreatureType.SPIDER);
		creatures_list.get(world).add(CreatureType.ZOMBIE);
		
		for (CreatureType type: creatures_list.get(world))
		{
			allcreatures_list.add(type);
			for (Class<?> creature: UndeadsNightDefaultConf.class.getDeclaredClasses())
			{
				if (creature.getSimpleName().equalsIgnoreCase(type.toString()))
				{
					try
					{
						creatures_map.get(world).put(type.toString(), (UndeadsNightDefaultConf)creature.newInstance());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	public void createPath()
	{
		new File(main_dir).mkdir();
		for (World w : plugin.getServer().getWorlds())
		{
			String path = main_dir + w.getName() + File.separator;
			new File(path).mkdir();
			creatures_conf.put(w.getName(), new File(path + "UndeadsNightCreatures.yml"));
			undeadsnight_conf.put(w.getName(), new File(path + "UndeadsNight.yml"));
			timer_conf.put(w.getName(), new File(path + "UndeadsDiggingTimer.yml"));
			initCreaturesList(w.getName());
		}
	}
	
	public boolean saveTimer(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving DiggingTimer for world \"" + world_name + "\"");
			timer_conf.get(world_name).createNewFile();
			FileWriter out = new FileWriter(timer_conf.get(world_name).getPath());
			out.append(new Yaml().dump(UndeadsDiggingTimerConf.getTime(world_name)));
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not save DiggingTimer for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveSpawn(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving Spawn for world \"" + world_name + "\"");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "enabled",
					UndeadsNightSpawnListener.isEnabled.get(world_name).get(), "spawn");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "randomTarget",
					UndeadsNightSpawnListener.randomTarget.get(world_name).get(), "spawn");
			for (CreatureType type: creatures_list.get(world_name))
				Config.save(creatures_conf.get(world_name).getPath(), "spawn",
						UndeadsNightSpawnListener.spawnBy.get(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.info("UndeadsNight: Could not save Spawn for world \"" + world_name + "\"");
			return false;
		}
		return true;
	}
	
	public boolean saveDigging(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving Digging for world \"" + world_name + "\"");
			Config.save(creatures_conf.get(world_name).getPath(), "explosiveDigging",
					UndeadsDigging.creeper_explode.get(world_name).get(), CreatureType.CREEPER.toString());
			for (CreatureType type: creatures_list.get(world_name))
				Config.save(creatures_conf.get(world_name).getPath(), "digging",
						UndeadsDigging.dig.get(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.info("UndeadsNight: Could not save Digging for world \"" + world_name + "\"");
			return false;
		}
		return true;
	}
	
	public boolean saveDarkness(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving Darkness for world \"" + world_name + "\"");
			for (CreatureType type: creatures_list.get(world_name))
			{
				Config.save(creatures_conf.get(world_name).getPath(), "darknessRadius",
						UndeadsWantDarkness.radius.get(type, world_name), type.toString());
				Config.save(creatures_conf.get(world_name).getPath(), "darknessHeight",
						UndeadsWantDarkness.height.get(type, world_name), type.toString());
				Config.save(creatures_conf.get(world_name).getPath(), "wantsDarkness",
						UndeadsWantDarkness.wantsDarkness.get(type, world_name), type.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.info("UndeadsNight: Could not save Darkness for world \"" + world_name + "\"");
			return false;
		}
		return true;
	}
	
	public boolean saveLightning(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving Lightning for world \"" + world_name + "\"");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "enabled",
					UndeadsNightLightningStrikeListener.isEnabled.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "isUndeadsNight",
					UndeadsNightLightningStrikeListener.isUndeadsNight.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "percentage",
					UndeadsNightLightningStrikeListener.percentage.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "percentageNumber",
					UndeadsNightLightningStrikeListener.percentage_number.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "timeStartTry",
					UndeadsNightLightningStrikeListener.time_start.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "timeEndTry",
					UndeadsNightLightningStrikeListener.time_end.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "actualDuration",
					UndeadsNightLightningStrikeListener.actual_duration.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "durationMin",
					UndeadsNightLightningStrikeListener.duration_min.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "durationMax",
					UndeadsNightLightningStrikeListener.duration_max.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "explosionPower",
					UndeadsNightLightningStrikeListener.explosion_power.get(world_name).get(), "lightning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "explosionFire",
					UndeadsNightLightningStrikeListener.explosion_fire.get(world_name).get(), "lightning");
			for (CreatureType type: creatures_list.get(world_name))
				Config.save(creatures_conf.get(world_name).getPath(), "lightning",
						UndeadsNightLightningStrikeListener.spawnBy.get(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not save Lightning for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveBurning(String world_name)
	{
		try
		{
			log.info("UndeadsNight: saving Burning for world \"" + world_name + "\"");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "glassOpacity",
					UndeadsBurning.glass_opacity.get(world_name).get(), "burning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "leavesOpacity",
					UndeadsBurning.leaves_opacity.get(world_name).get(), "burning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "timeEndBurning",
					UndeadsBurning.night_from.get(world_name).get(), "burning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "timeStartBurning",
					UndeadsBurning.night_to.get(world_name).get(), "burning");
			Config.save(undeadsnight_conf.get(world_name).getPath(), "burningDuration",
					UndeadsBurning.time_burning.get(world_name).get(), "burning");
			Config.save(creatures_conf.get(world_name).getPath(), "explosionFire",
					UndeadsBurning.creeper_explosion_fire.get(world_name).get(), CreatureType.CREEPER.toString());
			Config.save(creatures_conf.get(world_name).getPath(), "explosionSize",
					UndeadsBurning.creeper_explosion_size.get(world_name).get(), CreatureType.CREEPER.toString());
			for (CreatureType type: creatures_list.get(world_name))
				Config.save(creatures_conf.get(world_name).getPath(), "burning",
						UndeadsBurning.burn.get(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not save Burning for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveGlobal()
	{
		Object tmp = new UndeadsBurning.DefaultConfig();
		if (Config.save("GlobalConfig.yml", tmp, "burning") == false)
			return false;
		tmp = new UndeadsDigging.DefaultConfig();
		if (Config.save("GlobalConfig.yml", tmp, "digging") == false)
			return false;
		tmp = new UndeadsWantDarkness.DefaultConfig();
		if (Config.save("GlobalConfig.yml", tmp, "darkness") == false)
			return false;
		tmp = new UndeadsNightLightningStrikeListener.DefaultConfig();
		if (Config.save("GlobalConfig.yml", tmp, "lightning") == false)
			return false;
		tmp = new UndeadsNightSpawnListener.DefaultConfig();
		return Config.save("GlobalConfig.yml", tmp, "spawn");
	}
	
	public void saveAll(boolean override)
	{
		if (override || ! (new File("GlobalConfig.yml")).exists())
			saveGlobal();
		for (World w: plugin.getServer().getWorlds())
		{
			if (! override &&
					undeadsnight_conf.get(w.getName()).exists() &&
					creatures_conf.get(w.getName()).exists())
				continue;
			saveTimer(w.getName());
			saveSpawn(w.getName());
			saveLightning(w.getName());
			saveBurning(w.getName());
			saveDigging(w.getName());
			saveDarkness(w.getName());
		}
	}
	
	public boolean loadCreatures(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Creatures for world \"" + world_name + "\"");
			Configuration config = Config.initConf(creatures_conf.get(world_name).getPath());
			if (config == null)
			{
				Print.log("null");
				return false;
			}
			config.load();
			Map<String, Object> nodes = config.getAll();
			for (String node: nodes.keySet())
			{
				try
				{
					addCreature(world_name, node.split("\\.")[0]);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Creatures for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadTimer(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading DiggingTimer for world \"" + world_name + "\"");
			FileInputStream in = new FileInputStream(timer_conf.get(world_name));
			HashMap<String, Integer> tmp;
			if ((tmp = (HashMap<String, Integer>) new Yaml().load(in)) == null)
				throw new NullPointerException();
			UndeadsDiggingTimerConf.setTime(world_name, tmp);
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load DiggingTimer for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean loadSpawn(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Spawn for world \"" + world_name + "\"");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "enabled",
					(Type<?>)UndeadsNightSpawnListener.isEnabled.get(world_name), "spawn");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "randomTarget",
					(Type<?>)UndeadsNightSpawnListener.randomTarget.get(world_name), "spawn");
			for (CreatureType type: creatures_list.get(world_name))
				Config.load(creatures_conf.get(world_name).getPath(), "spawn",
						UndeadsNightSpawnListener.spawnBy.getType(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Spawn for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean loadDigging(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Digging for world \"" + world_name + "\"");
			Config.load(creatures_conf.get(world_name).getPath(), "explosiveDigging",
					(Type<?>)UndeadsDigging.creeper_explode.get(world_name), CreatureType.CREEPER.toString());
			for (CreatureType type: creatures_list.get(world_name))
				Config.load(creatures_conf.get(world_name).getPath(), "digging",
						UndeadsDigging.dig.getType(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Digging for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean loadDarkness(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Darkness for world \"" + world_name + "\"");
			for (CreatureType type: creatures_list.get(world_name))
			{
				Config.load(creatures_conf.get(world_name).getPath(), "darknessRadius",
						UndeadsWantDarkness.radius.getType(type, world_name), type.toString());
				Config.load(creatures_conf.get(world_name).getPath(), "darknessHeight",
						UndeadsWantDarkness.height.getType(type, world_name), type.toString());
				Config.load(creatures_conf.get(world_name).getPath(), "wantsDarkness",
						UndeadsWantDarkness.wantsDarkness.getType(type, world_name), type.toString());
			}
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Darkness for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean loadLightning(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Lightning for world \"" + world_name + "\"");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "enabled",
					(Type<?>)UndeadsNightLightningStrikeListener.isEnabled.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "isUndeadsNight",
					(Type<?>)UndeadsNightLightningStrikeListener.isUndeadsNight.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "percentage",
					(Type<?>)UndeadsNightLightningStrikeListener.percentage.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "percentageNumber",
					(Type<?>)UndeadsNightLightningStrikeListener.percentage_number.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "timeStartTry",
					(Type<?>)UndeadsNightLightningStrikeListener.time_start.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "timeEndTry",
					(Type<?>)UndeadsNightLightningStrikeListener.time_end.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "actualDuration",
					(Type<?>)UndeadsNightLightningStrikeListener.actual_duration.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "durationMin",
					(Type<?>)UndeadsNightLightningStrikeListener.duration_min.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "duration_max",
					(Type<?>)UndeadsNightLightningStrikeListener.duration_max.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "explosionPower",
					(Type<?>)UndeadsNightLightningStrikeListener.explosion_power.get(world_name), "lightning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "explosionFire",
					(Type<?>)UndeadsNightLightningStrikeListener.explosion_fire.get(world_name), "lightning");
			for (CreatureType type: creatures_list.get(world_name))
				Config.load(creatures_conf.get(world_name).getPath(), "lightning",
						UndeadsNightLightningStrikeListener.spawnBy.getType(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Lightning for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean loadBurning(String world_name)
	{
		try
		{
			log.info("UndeadsNight: loading Burning for world \"" + world_name + "\"");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "glassOpacity",
					(Type<?>)UndeadsBurning.glass_opacity.get(world_name), "burning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "leavesOpacity",
					(Type<?>)UndeadsBurning.leaves_opacity.get(world_name), "burning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "timeEndBurning",
					(Type<?>)UndeadsBurning.night_from.get(world_name), "burning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "timeStartBurning",
					(Type<?>)UndeadsBurning.night_to.get(world_name), "burning");
			Config.load(undeadsnight_conf.get(world_name).getPath(), "burningDuration",
					(Type<?>)UndeadsBurning.time_burning.get(world_name), "burning");
			Config.load(creatures_conf.get(world_name).getPath(), "explosionFire",
					(Type<?>)UndeadsBurning.creeper_explosion_fire.get(world_name), CreatureType.CREEPER.toString());
			Config.load(undeadsnight_conf.get(world_name).getPath(), "explosionSize",
					(Type<?>)UndeadsBurning.creeper_explosion_size.get(world_name), CreatureType.CREEPER.toString());
			for (CreatureType type: creatures_list.get(world_name))
				Config.load(creatures_conf.get(world_name).getPath(), "burning",
						UndeadsBurning.burn.getType(type, world_name), type.toString());
		}
		catch(Exception e)
		{
			log.info("UndeadsNight: Could not load Burning for world \"" + world_name + "\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean loadGlobal()
	{
		Object tmp = new UndeadsBurning.DefaultConfig();
		Config.load("GlobalConfig.yml", tmp, "burning");
		tmp = new UndeadsDigging.DefaultConfig();
		Config.load("GlobalConfig.yml", tmp, "digging");
		tmp = new UndeadsWantDarkness.DefaultConfig();
		Config.load("GlobalConfig.yml", tmp, "darkness");
		tmp = new UndeadsNightLightningStrikeListener.DefaultConfig();
		Config.load("GlobalConfig.yml", tmp, "lightning");
		tmp = new UndeadsNightSpawnListener.DefaultConfig();
		Config.load("GlobalConfig.yml", tmp, "spawn");
		return true;
	}
	
	public void loadAll()
	{
		loadGlobal();
		for (World w : plugin.getServer().getWorlds())
		{
			loadCreatures(w.getName());
			loadTimer(w.getName());
			loadSpawn(w.getName());
			loadLightning(w.getName());
			loadBurning(w.getName());
			loadDigging(w.getName());
			loadDarkness(w.getName());
		}
	}
	
	public void setupConfig()
	{
		saveAll(false);
		loadAll();
	}
}
