package com.github.faviomc19.deathrun.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.faviomc19.deathrun.DeathRun;

public class SelectorListener implements Listener{
	
	DeathRun plugin;

	public SelectorListener(DeathRun plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!player.hasPermission("deathrun.admin"))
			return;
		
		if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		

		if(event.getItem() == null || event.getItem().getType().equals(Material.AIR))
			return;
		
		ItemMeta meta = event.getItem().getItemMeta();
		
		if(meta.getLocalizedName() == null || !meta.getLocalizedName().equals("deathrun"))
			return;
		
		plugin.getManager().setSelectorPos(event.getPlayer(), event.getClickedBlock().getLocation(), event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? false : true);
		
		event.setCancelled(true);
	}
}
