package com.github.faviomc19.deathrun.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.traps.BaseTrap;

import io.netty.util.internal.ThreadLocalRandom;

public class PlayerListener implements Listener{
	
	DeathRun plugin;
	
	public PlayerListener(DeathRun plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		
		String name = event.getDismounted().getCustomName();
		
		if(name == null || !name.equals("TRAP"))
			return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		
		switch(event.getDamager().getType()) {
		case ARROW:{
			String name = event.getDamager().getCustomName();
			
			if(name == null || !name.equals("deathrun:arrow"))
				return;
			
			Vector velocity = player.getVelocity();
			double z = ThreadLocalRandom.current().nextDouble(velocity.getZ()+0.4, +1.0);
			double x = ThreadLocalRandom.current().nextDouble(velocity.getX()+0.4, +1.0);
			velocity.setX(x);
			velocity.setZ(z);
			player.setVelocity(velocity);
			break;
		}
		default:
			break;
		}
	}
	
	@EventHandler
	public void entityChange(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		if(entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase("deathrun:falling")) {
			event.setCancelled(true);
			event.getEntity().remove();
		}
	}
	
	@EventHandler
	public void onPressButtom(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		
		if(!plugin.getManager().buttoms.containsKey(event.getClickedBlock().getLocation()))
			return;
		
		BaseTrap trap = plugin.getManager().traps.get(plugin.getManager().buttoms.get(event.getClickedBlock().getLocation()));
		
		if(trap == null)
			return;
		
		plugin.getManager().initTrap(trap);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlaceButtom(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		ItemMeta meta = item.getItemMeta();
		
		if(meta.getLocalizedName() == null || !meta.getLocalizedName().startsWith("button:"))
			return;
		
		String name = meta.getLocalizedName().replaceAll("button:", "");
		
		BaseTrap trap = plugin.getManager().traps.get(name);
		
		if(trap == null)
			return;
		
		plugin.getManager().buttoms.put(event.getBlockPlaced().getLocation(), name);
		NekoConfig config = plugin.getConfigManager().traps;
		config.set("buttons."+plugin.getUtils().getCode(event.getBlockPlaced().getLocation()), name);
		config.saveConfig();
		plugin.getUtils().sendMessage(event.getPlayer(), "&aSe ha agregado el boton.");
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onBreakButtom(BlockBreakEvent event) {
		if(!plugin.getManager().buttoms.containsKey(event.getBlock().getLocation()))
			return;
		
		plugin.getManager().buttoms.remove(event.getBlock().getLocation());
		NekoConfig config = plugin.getConfigManager().traps;
		config.set("buttons."+plugin.getUtils().getCode(event.getBlock().getLocation()), null);
		config.saveConfig();
		plugin.getUtils().sendMessage(event.getPlayer(), "&cSe ha eliminado el boton.");
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		
		if(entity.getCustomName() == null || !entity.getCustomName().equals("deathrun:tnt"))
			return;
		
		event.blockList().clear();
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(plugin.getManager().check(event.getBlock().getLocation(), null))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY())
            return;
		
		Player player = event.getPlayer();
		
		if(!plugin.getManager().check(null, player))
			return;
		
		Block block = event.getTo().getBlock();
		
		if(block.getType().equals(Material.WATER))
			plugin.getManager().losePlayer(player);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		
		if(event.getFinalDamage() < player.getHealth())
			return;
		
		if(!plugin.getManager().check(null, player))
			return;
		
		event.setCancelled(true);
		plugin.getManager().losePlayer(player);
	}
}
