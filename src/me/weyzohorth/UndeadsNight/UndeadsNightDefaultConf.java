package me.weyzohorth.UndeadsNight;

import java.util.Map;

public class UndeadsNightDefaultConf
{
	public Boolean	burning = true;
	public Boolean	digging = true;
	public Boolean	darkness = true;
	public Integer	radius = 3;
	public Integer	height = 3;
	public Integer	lightning = 3;
	public Integer	spawn = 6;
	public Map<String, Integer>	blockList = null;
	
	static public class Creeper extends UndeadsNightDefaultConf 
	{
		public Integer	explosionSize = 3;
		public Integer	poweredExplosionSize = 6;
		public Boolean	explosionFire = false;
		public Boolean	diggingExplode = true;
		public Integer	diggingDistance = 3;
		public Integer	diggingPoweredDistance = 6;
		
		Creeper()
		{
			lightning = 3;
			spawn = 2;
			burning = false;
		}
	}
	
	static public class Ghast extends UndeadsNightDefaultConf 
	{	
		Ghast()
		{
			lightning = 1;
			spawn = 2;
			digging = false;
		}
	}
	
	static public class Giant extends UndeadsNightDefaultConf 
	{	
		Giant()
		{
			lightning = 1;
			digging = false;
		}
	}
	
	static public class Pig_Zombie extends UndeadsNightDefaultConf 
	{	
		Pig_Zombie()
		{
			lightning = 5;
			spawn = 2;
			burning = false;
		}
	}
	
	static public class Skeleton extends UndeadsNightDefaultConf 
	{	
		Skeleton()
		{
			lightning = 4;
		}
	}
	
	static public class Slime extends UndeadsNightDefaultConf 
	{	
		Slime()
		{
			lightning = 2;
			spawn = 2;
			burning = false;
			digging = false;
		}
	}
	
	static public class Spider extends UndeadsNightDefaultConf 
	{	
		Spider()
		{
			lightning = 3;
			burning = false;
			digging = false;
		}
	}
	
	static public class Zombie extends UndeadsNightDefaultConf 
	{	
		Zombie()
		{
			lightning = 5;
			spawn = 5;
			burning = false;
		}
	}
}
