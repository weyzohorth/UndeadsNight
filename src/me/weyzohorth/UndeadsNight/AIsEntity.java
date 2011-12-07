package me.weyzohorth.UndeadsNight;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Player;

public abstract class AIsEntity
{
	abstract public boolean is(Entity entity);
	
	static class IsZombie extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isZombie(entity);
		}
	}
	
	static class IsPigZombie extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isPigZombie(entity);
		}
	}
	
	static class IsSkeleton extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isSkeleton(entity);
		}
	}
	
	static class IsSpider extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isSpider(entity);
		}
	}
	
	static class IsSlime extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isSlime(entity);
		}
	}
	
	static class IsGhast extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isGhast(entity);
		}
	}
	
	static class IsGiant extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isGiant(entity);
		}
	}
	
	static class IsCreeper extends AIsEntity
	{
		public boolean is(Entity entity)
		{
			return isCreeper(entity);
		}
	}
	
	static public boolean isZombie(Entity entity)
	{
		if (entity instanceof Zombie)
			return true;
		return false;
	}

	static public boolean isPigZombie(Entity entity)
	{
		if (entity instanceof PigZombie)
			return true;
		return false;
	}
	
	static public boolean isCreeper(Entity entity)
	{
		if (entity instanceof Creeper)
			return true;
		return false;
	}
	
	static public boolean isSpider(Entity entity)
	{
		if (entity instanceof Spider)
			return true;
		return false;
	}
	
	static public boolean isSkeleton(Entity entity)
	{
		if (entity instanceof Skeleton)
			return true;
		return false;
	}
	
	static public boolean isGhast(Entity entity)
	{
		if (entity instanceof Ghast)
			return true;
		return false;
	}
	
	static public boolean isSlime(Entity entity)
	{
		if (entity instanceof Slime)
			return true;
		return false;
	}
	
	static public boolean isGiant(Entity entity)
	{
		if (entity instanceof Giant)
			return true;
		return false;
	}
	
	static public boolean isMonster(Entity entity)
	{
		if (entity instanceof Monster)
			return true;
		return false;
	}
	
	static public boolean isPlayer(Entity entity)
	{
		if (entity instanceof Player)
			return true;
		return false;
	}
}
