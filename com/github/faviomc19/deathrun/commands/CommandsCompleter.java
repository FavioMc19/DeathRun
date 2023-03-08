package com.github.faviomc19.deathrun.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.faviomc19.deathrun.DeathRun;

public class CommandsCompleter implements TabCompleter {
	
	DeathRun plugin;
	
	public CommandsCompleter(DeathRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
		
		if(!s.hasPermission("deathrun.admin")) {
			return new ArrayList<String>();
		}
		
		if(a.length == 1) {
			return getArguments(List.of("createTrap", "boton", "item", "createZone", "deleteTrap", "deleteZone", "reload"), a[0]);
		}
		
		switch(a[0]) {
		case "boton":
			return buttonCompleter(a);
		case "createTrap":
			return createCompleter(a);
		case "deleteTrap":
			return getArguments(plugin.getManager().traps.keySet(), a[1]);
		case "deleteZone":
			return getArguments(plugin.getManager().zones.keySet(), a[1]);
		}
		
		return new ArrayList<String>();
	}
	
	private List<String> createCompleter(String[] a) {
		if(a.length == 2)
			return getArguments(List.of("flechas", "barcos", "falling", "tnt", "phantom", "pared", "rayos"), a[1]);
		
		return new ArrayList<String>();
	}
	
	private List<String> buttonCompleter(String[] a) {
		if(a.length == 2)
			return getArguments(plugin.getManager().traps.keySet(), a[1]);
		
		return new ArrayList<String>();
	}

	private List<String> getArguments(Set<String> keySet, String arg) {
		List<String> allcommands = new ArrayList<String>();
		allcommands.addAll(keySet);
		if(arg.isBlank() || arg.isEmpty())
			return allcommands;
		
		List<String> endArgs = new ArrayList<String>();
		for(String text : allcommands) {
			if(text.toLowerCase().startsWith(arg.toLowerCase())) {
				endArgs.add(text);
			}
		}
		return endArgs;
	}

	public List<String> getArguments(List<String> allcommands, String arg){
		if(arg.isBlank() || arg.isEmpty())
			return allcommands;
		
		List<String> endArgs = new ArrayList<String>();
		for(String text : allcommands) {
			if(text.toLowerCase().startsWith(arg.toLowerCase())) {
				endArgs.add(text);
			}
		}
		return endArgs;
	}
	
}
