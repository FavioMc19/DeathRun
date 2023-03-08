package com.github.faviomc19.deathrun.traps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.faviomc19.deathrun.DeathRun;

public abstract class BaseTrap {
	DeathRun plugin;
	private Location location;
	private int min_distance;
	private int cooldown;
	private int current_cooldown;
	private boolean actived;
	private boolean is_buttom;
	private List<Location> buttoms;
	protected String name;
	
	public BaseTrap(DeathRun plugin, Location location) {
		this.plugin = plugin;
		this.location = location;
		current_cooldown = 0;
		actived = false;
		is_buttom = false;
		buttoms = new ArrayList<Location>();
	}
	
	public void setMinDistance(int min_distance) {
		this.min_distance = min_distance;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public void setActived(boolean actived) {
		this.actived = actived;
	}
	
	public void setButtomMode(Boolean is_buttom) {
		this.is_buttom = is_buttom;
	}
	
	public void setButtoms(List<Location> buttoms) {
		this.buttoms = buttoms;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addButtom(Location location) {
		if(buttoms.contains(location))
			return;
		
		buttoms.add(location);
	}
	
	public boolean isActived() {
		return actived;
	}
	
	public boolean isButtomMode() {
		return is_buttom;
	}
	
	public int getMinDistance() {
		return min_distance;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public boolean inArea(Player player) {
		return player.getLocation().distance(location) < min_distance;
	}
	
	public abstract void initTrap();
	
	public abstract void resetTrap();
	
	public abstract void time();
	
	public abstract String type();
	
	public void runTime() {
		current_cooldown++;
		
		if(current_cooldown >= cooldown) {
			resetTrap();
			current_cooldown = 0;
			actived = false;
			return;
		}
		
		time();
	}
}
