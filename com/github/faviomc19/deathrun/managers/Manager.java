package com.github.faviomc19.deathrun.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.Selector;
import com.github.faviomc19.deathrun.traps.BaseTrap;

public class Manager {
	
	DeathRun plugin;
	
	public Manager(DeathRun plugin) {
		this.plugin = plugin;
	}
	
	public Map<Player, Selector> selectors = new HashMap<Player, Selector>();
	public Map<String, BaseTrap> traps = new HashMap<String, BaseTrap>();
	public Map<Location, String> buttoms = new HashMap<Location, String>();
	public Map<String, Selector> zones = new HashMap<String, Selector>();
	public List<BaseTrap> running_traps = new ArrayList<BaseTrap>();
	
	BukkitTask task;
	
	public boolean initTrap(BaseTrap trap) {
		if(running_traps.contains(trap))
			return false;
		
		trap.initTrap();
		running_traps.add(trap);
		initTask();
		
		return true;
	}
	
	public boolean check(Location location, Player player) {
		if(player != null && location == null && inZone(player)) {
			switch(plugin.getConfigManager().mode) {
			case GAMEMODE:
				return plugin.getConfigManager().gamemodes.contains(player.getGameMode().toString());
			case PERMISSION:
				return player.hasPermission(plugin.getConfigManager().permission);
			}
		}
		
		return inZone(location);
	}
	
	public boolean inZone(Player player) {
		return inZone(player.getLocation());
	}
	
	public boolean inZone(Location location) {
		for(Selector selector : zones.values()) {
			if(selector.inZone(location))
				return true;
		}
			
		return false;
	}
	
	public void initTask() {
		if(task != null)
			return;
		
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {

			@Override
			public void run() {
				if(running_traps.isEmpty()) {
					task.cancel();
					task = null;
					return;
				}
				
				Iterator<BaseTrap> iterator = running_traps.iterator();
				
				while(iterator.hasNext()) {
					BaseTrap trap = iterator.next();
					if(!trap.isActived()) {
						iterator.remove();
						continue;
					}
					
					trap.runTime();
				}
			}
			
		}, 0, 10L);
	}
	
	public void setSelectorPos(Player player, Location location, boolean first) {
		Selector selector = selectors.getOrDefault(player, new Selector());
		
		String loc = "("+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+")";
		
		if(first && !location.equals(selector.getPos1())) {
			selector.setPos1(location);
			plugin.getUtils().sendMessage(player, "&ePosicion 1 establecida. &7"+loc);
		}
		
		if(!first && !location.equals(selector.getPos2())) {
			selector.setPos2(location);
			plugin.getUtils().sendMessage(player, "&ePosicion 2 establecida. &7"+loc);
		}
		
		selector.draw();
		selectors.put(player, selector);
	}
	
	public void losePlayer(Player player) {
		Bukkit.broadcastMessage(plugin.getUtils().color(plugin.getConfigManager().message_eliminated.replaceAll("%player%", player.getName())));
		player.setGameMode(GameMode.SPECTATOR);
	}
}
