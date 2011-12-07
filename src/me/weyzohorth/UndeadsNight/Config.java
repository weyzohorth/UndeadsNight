package me.weyzohorth.UndeadsNight;

import java.lang.reflect.Field;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.util.config.Configuration;

public class Config
{
	static public Configuration initConf(String filename)
	{
		File configFile;
		if (filename.contains("/") || filename.contains(File.separator))
			configFile = new File(filename);
		else
			configFile = new File("plugins/UndeadsNight/" + File.separator + filename);
		if (! configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		return new Configuration(configFile);
	}
	
	static private boolean saveField(Configuration config, Object obj, Field field, String node)
	{
		String line = field.getName();
		try
		{
			if (node != null && node != "")
				line = node + "." + line;
			config.setProperty(line, field.get(obj));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	static private boolean loadField(Configuration config, Object obj, Field field, String node)
	{
		String line = field.getName();
		try
		{
			if (node != null && node != "")
				line = node + "." + line;
			Object value = field.get(obj);
			if (value instanceof Integer)
				field.set(obj, config.getInt(line, (Integer)value));
			else if (value instanceof Boolean)
				field.set(obj, config.getBoolean(line, (Boolean)value));
			else if (value instanceof Double)
				field.set(obj, config.getDouble(line, (Double)value));
			else if (value instanceof String)
				field.set(obj, config.getString(line, (String)value));
			else if (value instanceof List)
				field.set(obj, config.getList(line));
			else
				return false;
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	static public boolean save(String filename, Object obj, String node)
	{
		Configuration config = initConf(filename);
		if (config == null)
			return false;
		config.load();
		for (Field f: obj.getClass().getDeclaredFields())
			saveField(config, obj, f, node);
		config.save();
		return true;
	}
	
	static public boolean save(String filename, String field, Object val, String node)
	{
		Configuration config = initConf(filename);
		if (config == null)
			return false;
		config.load();
		if (node != null && node != "")
			field = node + "." + field;
		config.setProperty(field, val);
		config.save();
		return true;
	}
	
	static public boolean load(String filename, Object obj, String node)
	{
		Configuration config = initConf(filename);
		if (config == null)
			return false;
		config.load();
		for (Field f: obj.getClass().getDeclaredFields())
			loadField(config, obj, f, node);
		return true;
	}
	
	static public Object load(String filename, String field, String node)
	{
		Configuration config = initConf(filename);
		if (config == null)
			return null;
		config.load();
		if (node != null && node != "")
			field = node + "." + field;
		Map<String, Object> map = config.getAll();
		if (! map.containsKey(field))
			return null;
		return map.get(field);
	}
	
	static public boolean load(String filename, String field, Type<?> val, String node)
	{
		Configuration config = initConf(filename);
		if (config == null)
			return false;
		config.load();
		if (node != null && node != "")
			field = node + "." + field;
		Map<String, Object> map = config.getAll();
		if (! map.containsKey(field))
			return false;
		val.set(map.get(field));
		return true;
	}
}
