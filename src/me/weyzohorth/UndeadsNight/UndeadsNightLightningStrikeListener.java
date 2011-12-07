package me.weyzohorth.UndeadsNight;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.weather.WeatherListener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class UndeadsNightLightningStrikeListener extends WeatherListener
{
	static public class DefaultConfig
	{
		static public Boolean	isEnabled = true;
		static public Boolean	isUndeadsNight = false;
		static public Integer	percentage = 1000;
		static public Integer	percentage_number = 500;
		static public Integer	timeStart = 12000;
		static public Integer	timeEnd = 14000;
		static public Integer	actualDuration = 0;
		static public Integer	durationMin = 12000;
		static public Integer	durationMax = 48000;
		static public Boolean	explosionFire = true;
		static public Integer	explosionPower = 3;
		static public Integer	creatureSpawnsBy = 5;
	}
	public UndeadsNight plugin;
	static private boolean isInit = false;
	static public List<World> worlds;
	static public Map<String, IType> isEnabled = new HashMap<String, IType>();
	static public Map<String, IType> isUndeadsNight = new HashMap<String, IType>();
	static public Map<String, IType> percentage = new HashMap<String, IType>();
	static public Map<String, IType> percentage_number = new HashMap<String, IType>();
	static public Map<String, IType> time_start = new HashMap<String, IType>();
	static public Map<String, IType> time_end = new HashMap<String, IType>();
	static public Map<String, IType> actual_duration = new HashMap<String, IType>();
	static public Map<String, IType> duration_min = new HashMap<String, IType>();
	static public Map<String, IType> duration_max = new HashMap<String, IType>();
	static public Map<String, IType> explosion_fire = new HashMap<String, IType>();
	static public Map<String, IType> explosion_power = new HashMap<String, IType>();
	static public UndeadsNightCreaturesMap<Integer>	spawnBy;
	
	public UndeadsNightLightningStrikeListener(UndeadsNight plugin)
	{
		this.plugin = plugin;
		worlds = plugin.getServer().getWorlds();
		initMap();
	}
	
	private void initMap()
	{
		if (isInit)
			return ;
		spawnBy = new UndeadsNightCreaturesMap<Integer>(plugin.getServer(), 5, "lightning");
		for (World w: worlds)
		{
			isEnabled.put(w.getName(), new Type<Boolean>(DefaultConfig.isEnabled));
			isUndeadsNight.put(w.getName(), new Type<Boolean>(DefaultConfig.isUndeadsNight));
			percentage.put(w.getName(), new Type<Integer>(DefaultConfig.percentage));
			percentage_number.put(w.getName(), new Type<Integer>(DefaultConfig.percentage_number));
			time_start.put(w.getName(), new Type<Integer>(DefaultConfig.timeStart));
			time_end.put(w.getName(), new Type<Integer>(DefaultConfig.timeEnd));
			actual_duration.put(w.getName(), new Type<Integer>(DefaultConfig.actualDuration));
			duration_min.put(w.getName(), new Type<Integer>(DefaultConfig.durationMin));
			duration_max.put(w.getName(), new Type<Integer>(DefaultConfig.durationMax));
			explosion_fire.put(w.getName(), new Type<Boolean>(DefaultConfig.explosionFire));
			explosion_power.put(w.getName(), new Type<Integer>(DefaultConfig.explosionPower));
			
		}
		isInit = true;
	}
	
	public void startUndeadsNight(World w)
	{
		isUndeadsNight.get(w.getName()).set(true);
		isEnabled.get(w.getName()).set(true);
		Random rand = new Random();
		w.setStorm(true);
		w.setThundering(true);
		actual_duration.get(w.getName()).set((Integer)duration_min.get(w.getName()).get() +
				rand.nextInt((Integer)duration_max.get(w.getName()).get() - (Integer)duration_min.get(w.getName()).get()));
		w.setWeatherDuration((int)((Integer)(actual_duration.get(w.getName()).get()) * 1.3));
		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Undeads Night has begun in " + w.getName() + "!");
		percentage_number.get(w.getName()).set(rand.nextInt((Integer)percentage.get(w.getName()).get()));
		plugin.getConf().saveLightning(w.getName());
	}
	
	public void startUndeadsNightAllWorlds()
	{
		for (World w: worlds)
			if (w.getEnvironment() != Environment.NETHER)
			startUndeadsNight(w);
	}
	
	public void onWeatherChange(WeatherChangeEvent e)
	{
		World w = e.getWorld();
		if (w.isThundering() == false || w.hasStorm() == false)
		{
			isUndeadsNight.get(w.getName()).set(false);
			actual_duration.get(w.getName()).set(0);
			plugin.getConf().saveLightning(w.getName());
		}
	}
	
	public void onLightningStrike(LightningStrikeEvent e)
	{
		LightningStrike strike = e.getLightning();
		String name = strike.getWorld().getName();
		if ((Boolean)isUndeadsNight.get(name).get() == true &&
				strike.getWorld().getWeatherDuration() - (Integer)actual_duration.get(name).get() < 0)
		{
			isUndeadsNight.get(name).set(false);
			actual_duration.get(name).set(0);
			plugin.getConf().saveLightning(name);
		}
		if ((Boolean)isEnabled.get(name).get() == false || (Boolean)isUndeadsNight.get(name).get() == false)
			return ;
		Location loc = strike.getLocation();
		for (CreatureType type: UndeadsNightConf.creatures_list.get(name))
			plugin.getSListener().spawnMob(type, loc, spawnBy.get(type, name));
		loc.add(0, 0, -1);
		strike.getWorld().createExplosion(loc, (Integer)explosion_power.get(name).get(), false);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException err)
		{
			err.printStackTrace();
		}
		strike.getWorld().createExplosion(loc, (Integer)explosion_power.get(name).get() + 1, (Boolean)explosion_fire.get(name).get());
	}
}
