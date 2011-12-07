package me.weyzohorth.UndeadsNight;
 
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class UndeadsNight extends JavaPlugin
{
	Logger 								log = Logger.getLogger("Minecraft");
	private UndeadsBurning				burning;
	private UndeadsDigging				digging;
	private UndeadsDiggingManager		diggingManager;
	private UndeadsWantDarkness			darkness;
	private UndeadsEntityLoop			loop;
	private UndeadsNightSpawnListener	spawnListener;
	private UndeadsNightLightningStrikeListener	lsListener;
	private UndeadsBurningListener		burningListener;
	private UndeadsNightConf			conf;
	private UndeadsDiggingTimerConf		timerConf;
	private UndeadsNightCommand			UNCommand;

	//Permissions
	public static PermissionHandler permissionHandler;
	public boolean permissions = false;
	 
	private void setupPermissions() 
	 {
	       Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	       if (permissionHandler == null)
	       {
	           if (permissionsPlugin != null)
	           {
	               permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	               permissions = true;
	           }
	           else
	           {
	               log.info("Permission system not detected, defaulting to OP");	               
	           }
	       }
	 }
	
	@Override
	public void onEnable()
	{ 
		init();
		setupPermissions();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.CREATURE_SPAWN, spawnListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, spawnListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_COMBUST, burningListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.EXPLOSION_PRIME, burningListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.LIGHTNING_STRIKE, lsListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.WEATHER_CHANGE, lsListener, Event.Priority.Normal, this);
		conf.setupConfig();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new UndeadsNightLoop(this), 0, 10);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, loop, 0, 3);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, diggingManager, 30, 2);
		log.info("UndeadsNight has been enabled.");
	}

	@Override
	public void onDisable()
	{
		//loop.run = false;
		conf.saveAll(true);
		log.info("UndeadsNight has been disabled.");
	}
	
	public void init()
	{
		Print.log = log;
		Print.server = getServer();
		timerConf = new UndeadsDiggingTimerConf(this);
		conf = new UndeadsNightConf(this);
		burning = new UndeadsBurning(this);
		spawnListener = new UndeadsNightSpawnListener(this);
		lsListener = new UndeadsNightLightningStrikeListener(this);
		burningListener = new UndeadsBurningListener(this);
		UNCommand = new UndeadsNightCommand(this);
		loop = new UndeadsEntityLoop(this);
		diggingManager = new UndeadsDiggingManager(this);
		digging = loop.digging();
		darkness = loop.darkness();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!permissions)
		{
			if(!sender.isOp())
			{
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use that!");
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("help"))
		{
			sender.sendMessage("/UndeadsNight - Start the Undeads Night");
			sender.sendMessage("/UndeadsSleep - Stop the Undeads Night (lightning and spawn)");
			sender.sendMessage("/lightning help - Help to use lightning command");
			sender.sendMessage("/spawn help - Help to use spawn command");
			sender.sendMessage("/burning help - Help to use burning command");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("isUndeadsNight"))
		{
			for (World w : sender.getServer().getWorlds())
			{
				if (w.getEnvironment()!= Environment.NETHER)
					lsListener.startUndeadsNight(w);
				w.setTime(12500);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("UndeadsSleep"))
		{
			getServer().broadcastMessage(ChatColor.GRAY + "Undeads finished their strike back.");
			for (World w : sender.getServer().getWorlds())
			{	
				if (w.getEnvironment()!= Environment.NETHER)
				{
					w.setStorm(false);
					w.setThundering(false);
					UndeadsNightLightningStrikeListener.isEnabled.get(w.getName()).set(false);
					UndeadsNightLightningStrikeListener.isUndeadsNight.get(w.getName()).set(false);
				}
				w.setTime(0);
				UndeadsNightSpawnListener.isEnabled.get(w.getName()).set(false);
			}
			return true;
		}
		return UNCommand.execute(sender, cmd, args); 
	}
	
	public UndeadsNightLightningStrikeListener getLSListener()
	{
		return lsListener;
	}
	
	public UndeadsNightSpawnListener getSListener()
	{
		return spawnListener;
	}
	
	public UndeadsBurningListener getBListener()
	{
		return burningListener;
	}

	public UndeadsDigging getDigging()
	{
		return digging;
	}
	
	public UndeadsDiggingManager getDiggingManager()
	{
		return diggingManager;
	}
	
	public UndeadsWantDarkness getDarkness()
	{
		return darkness;
	}
	
	public UndeadsBurning getBurning()
	{
		return burning;
	}
	
	public UndeadsNightConf getConf()
	{
		return conf;
	}
	
	public UndeadsDiggingTimerConf getTimerConf()
	{
		return timerConf;
	}
}
