package me.weyzohorth.UndeadsNight;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class UndeadsNightLoop implements Runnable
{
	public UndeadsNight plugin;

	UndeadsNightLoop(UndeadsNight plugin)
	{
		this.plugin = plugin;
	}

	public void run()
	{
		UndeadsNightLightningStrikeListener lslistener;
		lslistener = plugin.getLSListener();
		for (World w : plugin.getServer().getWorlds())
		{
			String name = w.getName();
			for (Entity e: w.getEntities())
			{
				try
				{
					UndeadsBurning burning = new UndeadsBurning((LivingEntity)e);
					burning.tryBurning();
				}
				catch (Exception err)
				{
				}
			}
			if ((Boolean)UndeadsNightLightningStrikeListener.isEnabled.get(name).get() && w.getEnvironment() != Environment.NETHER)
			{
				if ((Boolean)UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).get())
				{
					if (w.hasStorm() == false || w.isThundering() == false)
						UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).set(false);
					else if ((Boolean)UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).get() && 18000 < w.getTime())
						w.setTime(18000);
					else if ((Boolean)UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).get() && w.getTime() < 18000)
						w.setTime(w.getTime() + 100);
				}
				if (1 <= (Integer)UndeadsNightLightningStrikeListener.percentage.get(name).get() &&
						(Boolean)UndeadsNightLightningStrikeListener.isEnabled.get(name).get() &&
						(Boolean)UndeadsNightLightningStrikeListener.isUndeadsNight.get(name).get() == false)
				{
					Random rand = new Random();
					long time = w.getTime();
					if ((Integer)UndeadsNightLightningStrikeListener.time_start.get(name).get() < time &&
							time < (Integer)UndeadsNightLightningStrikeListener.time_end.get(name).get() &&
							rand.nextInt((Integer)UndeadsNightLightningStrikeListener.percentage.get(name).get()) ==
								(Integer)UndeadsNightLightningStrikeListener.percentage_number.get(name).get())
						lslistener.startUndeadsNightAllWorlds();
				}

			}
		}
	}
}
