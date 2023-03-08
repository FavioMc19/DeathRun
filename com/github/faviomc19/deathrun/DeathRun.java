package com.github.faviomc19.deathrun;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.faviomc19.deathrun.commands.Commands;
import com.github.faviomc19.deathrun.commands.CommandsCompleter;
import com.github.faviomc19.deathrun.listeners.PlayerListener;
import com.github.faviomc19.deathrun.listeners.SelectorListener;
import com.github.faviomc19.deathrun.managers.ConfigManager;
import com.github.faviomc19.deathrun.managers.Manager;
import com.github.faviomc19.deathrun.utils.Utils;

public class DeathRun extends JavaPlugin{
	
	private Manager manager;
	private Utils utils;
	private ConfigManager configmanager;

	
	public void onEnable() {
		initClass();
		initCommands();
		initListeners();
	}
	
	public void initClass() {
		manager = new Manager(this);
		utils = new Utils(this);
		configmanager= new ConfigManager(this);
		loadConfig();
	}
	
	public void initCommands() {
		getCommand("deathrun").setExecutor(new Commands(this));
		getCommand("deathrun").setTabCompleter(new CommandsCompleter(this));
	}
	
	public void initListeners() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new SelectorListener(this), this);
	}
	
	public void loadConfig() {
		configmanager.loadTraps();
		configmanager.loadConfig();
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public Utils getUtils() {
		return utils;
	}
	
	public ConfigManager getConfigManager() {
		return configmanager;
	}
}
