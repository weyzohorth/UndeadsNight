package me.weyzohorth.UndeadsNight;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UndeadsEntityLoop implements Runnable
{
	static private UndeadsNight plugin;
	//private Thread thread;
	private UndeadsDigging digging;
	private UndeadsWantDarkness darkness;
	static public boolean run = true;
	private int player_id = 0;
	private int entity_id = 0;
	
	UndeadsEntityLoop(UndeadsNight plug)
	{
		plugin = plug;
		digging = new UndeadsDigging(plug);
		darkness = new UndeadsWantDarkness(plug);
		//thread = new Thread(this);
		//thread.start();
	}
	
	public void run_thread()
	{
		try
		{
			for (Player p: plugin.getServer().getOnlinePlayers())
				for (Entity e: p.getNearbyEntities(40, 40, 40))
					if (AIsEntity.isMonster(e))
					{
						darkness.darkness(e);
						digging.dig(e);
					}
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			Player[] players_list = plugin.getServer().getOnlinePlayers();
			if (players_list.length == 0)
				return ;
			player_id %= players_list.length;
			List<Entity> entity_list = players_list[player_id].getNearbyEntities(100, 100, 100);
			for (int i = 0; i < 20; i++)
			{
				if (entity_list.size() <= entity_id)
				{
					entity_id = 0;
					player_id++;
					return ;
				}
				if (AIsEntity.isMonster(entity_list.get(entity_id)))
				{
					darkness.darkness(entity_list.get(entity_id));
					digging.dig(entity_list.get(entity_id));
				}
				entity_id++;
			}
			//UndeadsDiggingManager.run();
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
	public UndeadsDigging digging()
	{
		return digging;
	}
	
	public UndeadsWantDarkness darkness()
	{
		return darkness;
	}
}
