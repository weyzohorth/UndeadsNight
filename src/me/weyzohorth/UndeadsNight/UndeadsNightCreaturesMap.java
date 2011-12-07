package me.weyzohorth.UndeadsNight;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.Server;

public class UndeadsNightCreaturesMap<T>
{
	static private Server	server;
	private Map<CreatureType, Map<String, IType>>	map = new HashMap<CreatureType, Map<String, IType>>();
	private T				value;
	private String			field = null;
	
	UndeadsNightCreaturesMap(Server serv, T val)
	{
		server = serv;
		value = val;
		initMap();
	}
	
	UndeadsNightCreaturesMap(Server serv, T val, String field)
	{
		server = serv;
		value = val;
		this.field = field;
		initMap();
	}
	
	public void initMap()
	{
		for (World w: server.getWorlds())
			for (CreatureType key: UndeadsNightConf.creatures_list.get(w.getName()))
				put(key, w.getName());
	}
	
	@SuppressWarnings("unchecked")
	public void put(CreatureType type, String world)
	{
		T val = value;
		if (field != null && field != "" &&
				UndeadsNightConf.creatures_map.containsKey(world) &&
				UndeadsNightConf.creatures_map.get(world).containsKey(type.toString()))
			try
			{
				Field f = UndeadsNightConf.creatures_map.get(world).get(type.toString()).getClass().getField(field);
				val = (T)f.get(UndeadsNightConf.creatures_map.get(world).get(type.toString()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		put(type, world, val);
	}
	
	public void put(CreatureType type, String world, T val)
	{
		if (map.containsKey(type) == false)
			map.put(type, new HashMap<String, IType>());
		if (map.get(type).containsKey(world) == false)
			map.get(type).put(world, new Type<T>(val));
		else
			map.get(type).get(world).set(val);
	}
	
	public void put(Entity ent, T val)
	{
		put(Enum.getCreatureType(ent), ent.getWorld().getName(), val);
	}
	
	@SuppressWarnings("unchecked")
	public T get(CreatureType type, String world)
	{
		if (containsKey(type, world) == false)
			return null;
		return (T)map.get(type).get(world).get();
	}
	
	public Map<String, IType> get(CreatureType type)
	{
		if (containsKey(type) == false)
			return null;
		return map.get(type);
	}
	
	public T get(Entity ent)
	{
		return get(Enum.getCreatureType(ent), ent.getWorld().getName());
	}
	
	public Type<?> getType(CreatureType type, String world)
	{
		if (containsKey(type, world) == false)
			return null;
		return (Type<?>)map.get(type).get(world);
	}
	
	public boolean containsKey(CreatureType type)
	{
		return map.containsKey(type);
	}
	
	public boolean containsKey(CreatureType type, String world)
	{
		if (map.containsKey(type) == false ||
				map.get(type).containsKey(world) == false)
			return false;
		return true;
	}
	
	public boolean remove(CreatureType type, String world)
	{
		if (containsKey(type, world) == false)
			return false;
		map.get(type).remove(world);
		return true;
	}
	
	public boolean remove(CreatureType type)
	{
		if (containsKey(type) == false)
			return false;
		map.remove(type);
		return true;
	}
	
	public boolean clear(CreatureType type)
	{
		if (containsKey(type) == false)
			return false;
		map.get(type).clear();
		return true;
	}
	
	public boolean clear()
	{
		map.clear();
		return true;
	}
}
