package me.weyzohorth.UndeadsNight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creeper;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class UndeadsDigging
{
	static public class DefaultConfig
	{
		static public Boolean	creatureDigging = true;
		static public Boolean	creeperExplode = true;
	}
	static private UndeadsNight			plugin;
	static boolean						isInit = false;
	static public Map<String, IType>	creeper_explode = new HashMap<String, IType>();
	static public UndeadsNightCreaturesMap<Boolean>	dig;
	
	UndeadsDigging(UndeadsNight plug)
	{
		plugin = plug;
		initMap();
	}
	
	void initMap()
	{
		if (isInit)
			return ;
		dig = new UndeadsNightCreaturesMap<Boolean>(plugin.getServer(), DefaultConfig.creatureDigging, "digging");
		for (World w: plugin.getServer().getWorlds())
			creeper_explode.put(w.getName(), new Type<Boolean>(DefaultConfig.creeperExplode));
		isInit = true;
	}
	
	static public boolean breakBlock(Block block, Entity entity)
	{
		if (block.getType() == Material.AIR ||
				block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ||
				block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
			return true;
		if (block.getType() == Material.OBSIDIAN || block.getType() == Material.BEDROCK)
			return false;
		if (AIsEntity.isCreeper(entity) && (Boolean)creeper_explode.get(entity.getWorld().getName()).get())
		{
			Double power = (Double)UndeadsBurning.creeper_explosion_size.get(entity.getWorld().getName()).get();
			if (((Creeper)entity).isPowered())
				power *= 2;
			if (((Creeper)entity).getTarget() == null ||
					power < Math.sqrt(Math.pow(entity.getLocation().getX() - ((Creeper)entity).getTarget().getLocation().getX(), 2) +
					Math.pow(entity.getLocation().getY() - ((Creeper)entity).getTarget().getLocation().getY(), 2) +
					Math.pow(entity.getLocation().getZ() - ((Creeper)entity).getTarget().getLocation().getZ(), 2)))
				return false;
			plugin.getServer().getPluginManager().callEvent(
					new ExplosionPrimeEvent(entity, power.floatValue(), false));
			block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, entity.getEntityId(), 10);
			Wait.wait(1000);
			entity.getWorld().createExplosion(entity.getLocation(), power.floatValue(), false);
		}
		else
		{
			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 10);
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
			block.setType(Material.AIR);
		}
		return true;
	}
	
	public void dig(Entity e)
	{
		if (dig.get(Enum.getCreatureType(e), e.getWorld().getName()) == false)
			return;
		Player player;
		try
		{
			player = (Player)((Monster)e).getTarget();
		}
		catch (Exception err)
		{
			return ;
		}
		if (player == null || e.isDead() || player.isDead() || ! player.isOnline())
			return;
		for (int i = 1; i <= 3; i++)
		{
			Location loc = e.getLocation();
			Location playerloc = player.getLocation();
			int x = 0;
			int z = 0;
			if ((i & 1) == 1)
			{
				if (loc.getBlockX() < playerloc.getBlockX())
					x = 1;
				else if (playerloc.getBlockX() < loc.getBlockX())
					x = -1;
				else continue;
			}
			if ((i & 2) == 2)
			{
				if (loc.getBlockZ() < playerloc.getBlockZ())
					z = 1;
				else if (playerloc.getBlockZ() < loc.getBlockZ())
					z = -1;
				else return;
			}
			if (e.getLocation().getBlockY() < playerloc.getBlockY())
			{
				if (! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(0, 2, 0)) ||
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(0, 1, 0)) ||
						// for zombie's safety, sand and gravel can suffocated them
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 2, z)) ||
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 1, z)))
					return;
			}
			else if (playerloc.getBlockY() < e.getLocation().getBlockY())
			{
				if (! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 1, z)) ||
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 0, z)) ||
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, -1, z)))
					return;
			}
			else
			{
				if (! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 1, z)) ||
						! UndeadsDiggingManager.breakBlock(e, e.getLocation().getBlock().getRelative(x, 0, z)))
					return;
			}
		}
	}
}
