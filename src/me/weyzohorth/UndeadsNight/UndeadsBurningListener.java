package me.weyzohorth.UndeadsNight;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

public class UndeadsBurningListener extends EntityListener
{
	public UndeadsNight 	plugin;
	public UndeadsBurning	burning;
	
	UndeadsBurningListener(UndeadsNight p)
	{
		plugin = p;
		burning = new UndeadsBurning(p);
	}
	
	public void stopBurning(EntityCombustEvent ev, Entity entity)
	{
		ev.setCancelled(true);
		stopBurning(entity);
	}
	public void stopBurning(Entity entity)
	{
		entity.setFireTicks(0);
	}
	
	public void onEntityCombust(EntityCombustEvent ev)
	{
		Entity	entity = ev.getEntity();
		String world_name = entity.getWorld().getName();
		if (UndeadsBurning.burn.containsKey(Enum.getCreatureType(entity)) &&
				! (Boolean)UndeadsBurning.burn.get(Enum.getCreatureType(entity)).get(world_name).get())
			stopBurning(ev, entity);
	}
	
	public void onExplosionPrimeEvent(ExplosionPrimeEvent ev)
	{
		if (AIsEntity.isCreeper(ev.getEntity()))
		{
			Creeper creeper = (Creeper)ev.getEntity();
			ev.setFire((Boolean)UndeadsBurning.creeper_explosion_fire.get(creeper.getWorld().getName()).get());
			ev.setRadius((Float)UndeadsBurning.creeper_explosion_size.get(creeper.getWorld().getName()).get());
			if (creeper.isPowered())
				ev.setRadius(ev.getRadius() * 2);
		}
	}
}
