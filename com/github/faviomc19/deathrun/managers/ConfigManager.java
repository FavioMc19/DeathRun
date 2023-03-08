package com.github.faviomc19.deathrun.managers;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.enums.Mode;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;
import com.github.faviomc19.deathrun.traps.ArrowTrap;
import com.github.faviomc19.deathrun.traps.BaseTrap;
import com.github.faviomc19.deathrun.traps.BoatTrap;
import com.github.faviomc19.deathrun.traps.FallingTrap;
import com.github.faviomc19.deathrun.traps.PhantomTrap;
import com.github.faviomc19.deathrun.traps.ThorTrap;
import com.github.faviomc19.deathrun.traps.TntTrap;
import com.github.faviomc19.deathrun.traps.WallTrap;

public class ConfigManager {
	
	DeathRun plugin;
	
	public ConfigManager(DeathRun plugin) {
		this.plugin = plugin;
	}
	
	public NekoConfig traps;
	public NekoConfig config;
	
	public Mode mode;
	public List<String> gamemodes;
	public String permission;
	public String message_eliminated;
	
	public void loadConfig() {
		config = new NekoConfig("config.yml", plugin);
		
		message_eliminated = config.getString("messages.eliminated", "&cEl jugador %player% fue eliminado.");
		
		permission = config.getString("permissions.player", "deathrun.player");
		
		mode = Mode.valueOf(config.getString("mode.mode", Mode.GAMEMODE.name()).toUpperCase());
		List<String> gamemodes = config.getStringList("mode.gamemodes", List.of("ADVENTURE", "SURVIVAL"));
		this.gamemodes = gamemodes.stream().map(String::toUpperCase).collect(Collectors.toList());
		
		config.update();
	}
	
	public void loadTraps() {
		traps = new NekoConfig("saved_traps.yml", plugin);
		
		if(traps.contains("traps")) {
			plugin.getManager().traps.clear();
			for(String name : traps.getConfigurationSection("traps").getKeys(false)) {
				String path = "traps."+name+".";
				String type = traps.getString(path+"type");
				BaseTrap trap = null;
				
				switch(type) {
				case "arrow":{
					int duration = traps.getInt(path+".duration");
					int arrows_by_seconds = traps.getInt(path+"arrows_by_seconds");
					trap = new ArrowTrap(plugin, null, arrows_by_seconds, duration);
					((ArrowTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((ArrowTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "boat":{
					int duration = traps.getInt(path+".duration");
					trap = new BoatTrap(plugin, null, duration);
					((BoatTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((BoatTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "falling":{
					int duration = traps.getInt(path+".duration");
					trap = new FallingTrap(plugin, null, duration);
					((FallingTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((FallingTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "tnt":{
					int duration = traps.getInt(path+".duration");
					int tnt_per_seconds = traps.getInt(path+"tnt_per_seconds");
					trap = new TntTrap(plugin, null, tnt_per_seconds, duration);
					((TntTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((TntTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "phantom":{
					int duration = traps.getInt(path+".duration");
					trap = new PhantomTrap(plugin, null, duration);
					((PhantomTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((PhantomTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "wall":{
					int duration = traps.getInt(path+".duration");
					trap = new WallTrap(plugin, null, duration);
					((WallTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((WallTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				case "thor":{
					int duration = traps.getInt(path+".duration");
					int thors_per_seconds = traps.getInt(path+"thors_per_seconds");
					trap = new ThorTrap(plugin, null, thors_per_seconds, duration);
					((ThorTrap)trap).setPos1(traps.getLocation(path+"pos1"));
					((ThorTrap)trap).setPos2(traps.getLocation(path+"pos2"));
					break;
				}
				}
				trap.setName(name);
				plugin.getManager().traps.put(name, trap);
			}
		}
		
		if(traps.contains("buttons")) {
			plugin.getManager().buttoms.clear();
			for(String code : traps.getConfigurationSection("buttons").getKeys(false)) {
				Location location = plugin.getUtils().getLocation(code);
				String name = traps.getString("buttons."+code);
				plugin.getManager().buttoms.put(location, name);
			}
		}
		
		if(traps.contains("zones")) {
			plugin.getManager().zones.clear();
			for(String name : traps.getConfigurationSection("zones").getKeys(false)) {
				Selector selector = new Selector(traps.getLocation("zones."+name+".pos1"), traps.getLocation("zones."+name+".pos2"));
				plugin.getManager().zones.put(name, selector);
			}
		}
	}
}
