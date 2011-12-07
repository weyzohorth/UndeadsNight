package me.weyzohorth.UndeadsNight;

import org.bukkit.entity.CreatureType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class Enum
{
	static public CreatureType getCreatureType(String str) throws IllegalArgumentException
	{
		for (CreatureType type: CreatureType.values())
			if (type.toString().equalsIgnoreCase(str))
				return type;
		//throw new IllegalArgumentException("Error: Creature type \"" + str + "\" doesn't exist.");
		return null;
	}
	
	static public CreatureType getCreatureType(Entity ent) throws IllegalArgumentException
	{
		String lower = ent.toString().toLowerCase();
		CreatureType save = null;
		for (CreatureType type: CreatureType.values())
		{
			String tmp = type.toString().toLowerCase();
			if (lower.indexOf(tmp) != -1 && (save == null || save.toString().length() < tmp.length()))
				save = type;
			if (save != null && lower.length() == tmp.length())
				break;
		}
		/*if (save == null)
			throw new IllegalArgumentException("Error: Entity \"" + ent.toString() + "\" is not a creature.");*/
		return save;
	}
	
	static public Material getMaterial(String str) throws IllegalArgumentException
	{
		for (Material type: Material.values())
			if (type.toString().equalsIgnoreCase(str))
				return type;
		throw new IllegalArgumentException("Error: Material \"" + str + "\" doesn't exist.");
	}
	
	static public Material getMaterial(Material mat) throws IllegalArgumentException
	{
		String lower = mat.toString().toLowerCase();
		Material save = null;
		for (Material type: Material.values())
		{
			String tmp = type.toString().toLowerCase();
			if (lower.indexOf(tmp) != -1 && (save == null || save.toString().length() < tmp.length()))
				save = type;
			if (save != null && lower.length() == tmp.length())
				break;
		}
		if (save == null)
			throw new IllegalArgumentException("Error: Material \"" + mat.toString() + "\" is not a material.");
		return save;
	}
}
