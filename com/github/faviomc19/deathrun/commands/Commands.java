package com.github.faviomc19.deathrun.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.faviomc19.deathrun.DeathRun;
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

public class Commands implements CommandExecutor {
	
	DeathRun plugin;
	
	public Commands(DeathRun deathRun) {
		this.plugin = deathRun;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if(!s.hasPermission("deathrun.admin")) {
			plugin.getUtils().sendMessage(s, "&cNo tienes acceso a este comando.");
			return true;
		}
		
		if(a.length == 0) {
			plugin.getUtils().sendMessage(s, "&cArgumentons insuficientes");
			return true;
		}
		
		switch(a[0].toLowerCase()) {
		case "item":
			return itemCommand(s, l, a);
		case "createTrap":
			return createCommand(s, l, a);
		case "iniciar":
			return initCommand(s, l, a);
		case "boton":
			return buttonCommand(s, l, a);
		case "createzone":
			return createZoneCommand(s, l, a);
		case "deletetrap":
			return deleteTrapCommand(s, l, a);
		case "deletezone":
			return deleteZoneCommand(s, l, a);
		case "reload":
			return reloadCommand(s, l, a);
		}
		
		return true;
	}
	
	private boolean reloadCommand(CommandSender s, String l, String[] a) {
		plugin.loadConfig();
		plugin.getUtils().sendMessage(s, "&aLa configuracion se recargo con exito!");
		return true;
	}

	private boolean deleteTrapCommand(CommandSender s, String l, String[] a) {
		if(a.length != 2) {
			plugin.getUtils().sendMessage(s, "&cError. usa /"+l+" deleteTrap <nombre>");
			return true;
		}
		
		String name = a[1].toLowerCase();
		
		NekoConfig config = plugin.getConfigManager().traps;
		config.set("traps."+name, null);
		config.saveConfig();
		plugin.getManager().traps.remove(name);
		
		plugin.getUtils().sendMessage(s, "&aTrampa borrada con exito.");
		return true;
	}
	
	private boolean deleteZoneCommand(CommandSender s, String l, String[] a) {
		if(a.length != 2) {
			plugin.getUtils().sendMessage(s, "&cError. usa /"+l+" deleteZone <nombre>");
			return true;
		}
		
		String name = a[1].toLowerCase();
		
		NekoConfig config = plugin.getConfigManager().traps;
		config.set("zones."+name, null);
		config.saveConfig();
		plugin.getManager().zones.remove(name);
		
		plugin.getUtils().sendMessage(s, "&aZona borrada con exito.");
		return true;
	}

	private boolean createZoneCommand(CommandSender s, String l, String[] a) {
		if(!(s instanceof Player)) {
			plugin.getUtils().sendMessage(s, "&cEste comando es solo para jugadores.");
			return true;
		}
		
		Player player = (Player)s;
		
		if(a.length != 2) {
			plugin.getUtils().sendMessage(s, "&cError. usa /"+l+" createzone <nombre>");
			return true;
		}
		
		String name = a[1];
		
		if(plugin.getManager().zones.containsKey(name.toLowerCase())) {
			plugin.getUtils().sendMessage(s, "&cEsta zona ya existe");
			return true;
		}
		
		Selector selector = plugin.getManager().selectors.get(player);
		
		if(selector == null || selector.getPos1() == null || selector.getPos2() == null) {
			plugin.getUtils().sendMessage(s, "&cDebes seleccionar la zona con el item: /"+l+" item");
			return true;
		}
		
		plugin.getManager().zones.put(name, selector);
		NekoConfig config = plugin.getConfigManager().traps;
		config.set("zones."+name+".pos1", selector.getLocation(true));
		config.set("zones."+name+".pos2", selector.getLocation(false));
		config.saveConfig();
		
		plugin.getUtils().sendMessage(s, "&aZona guardada con exito");
		return true;
	}

	private boolean buttonCommand(CommandSender s, String l, String[] a) {
		if(!(s instanceof Player)) {
			plugin.getUtils().sendMessage(s, "&cEste comando es solo para jugadores.");
			return true;
		}
		
		Player player = (Player)s;
		
		if(a.length != 2) {
			plugin.getUtils().sendMessage(s, "&cError. usa /"+l+" boton <nombre>");
			return true;
		}
		
		String name = a[1];
		
		BaseTrap trap = plugin.getManager().traps.get(name);
		
		if(trap == null) {
			plugin.getUtils().sendMessage(s, "&cEsta trampa no existe!");
			return true;
		}
		
		ItemStack item = new ItemStack(Material.STONE_BUTTON);
		ItemMeta meta = item.getItemMeta();
		meta.setLocalizedName("button:"+name);
		meta.setDisplayName(plugin.getUtils().color("&eDeathRun button: &7"+name));
		meta.setLore(List.of("", plugin.getUtils().color("&7tipo: &3"+trap.type()), plugin.getUtils().color("&7nombre: &3"+name)));
		item.setItemMeta(meta);
		
		player.getInventory().addItem(item);
		plugin.getUtils().sendMessage(s, "&aHas obtenido un boton!");
		return true;
	}

	private boolean initCommand(CommandSender s, String l, String[] a) {
		if(a.length != 2) {
			plugin.getUtils().sendMessage(s, "&cError. usa /"+l+" init <nombre>");
			return true;
		}
		
		String name = a[1];
		
		BaseTrap trap = plugin.getManager().traps.get(name);
		
		if(trap == null) {
			plugin.getUtils().sendMessage(s, "&cEsta trampa no existe!");
			return true;
		}
		
		boolean running = !plugin.getManager().initTrap(trap);
		
		plugin.getUtils().sendMessage(s, running ? "&cEsta trampa ya esta activa. espera unos segundos..." : "&aTrampa activada con exito!");
		return true;
	}

	private boolean createCommand(CommandSender s, String l, String[] a) {
		if(!(s instanceof Player)) {
			plugin.getUtils().sendMessage(s, "&cEste comando es solo para jugadores.");
			return true;
		}
		Player player = (Player)s;
		
		if(a.length != 3) {
			plugin.getUtils().sendMessage(player, "&cError. usa /"+l+" createTrap <tipo> <nombre>");
			return true;
		}
		
		Selector selector = plugin.getManager().selectors.get(player);
		
		if(selector == null || selector.getPos1() == null || selector.getPos2() == null) {
			plugin.getUtils().sendMessage(s, "&cDebes seleccionar la zona con el item: /"+l+" item");
			return true;
		}
		
		String type = a[1].toLowerCase();
		String name = a[2].toLowerCase();
		
		List<String> types = List.of("flechas", "barcos", "falling", "tnt", "phantom", "pared", "rayos");
		
		if(!types.contains(type)) {
			plugin.getUtils().sendMessage(s, "&cEl tipo "+type+" no existe.");
			return true;
		}
		
		if(plugin.getManager().traps.containsKey(name)) {
			plugin.getUtils().sendMessage(s, "&cEste nombre ya esta en uso");
			return true;
		}
		
		BaseTrap trap = null;
		
		if(type.equals("flechas")) {
			trap = new ArrowTrap(plugin, player.getLocation(), 10, 5);
			trap.setName(name);
			((ArrowTrap)trap).save(selector);
		}
		
		if(type.equals("barcos")) {
			trap = new BoatTrap(plugin, player.getLocation(), 5);
			trap.setName(name);
			((BoatTrap)trap).save(selector);
		}
		
		if(type.equals("falling")) {
			trap = new FallingTrap(plugin, player.getLocation(), 10);
			trap.setName(name);
			((FallingTrap)trap).save(selector);
		}
		
		if(type.equals("tnt")) {
			trap = new TntTrap(plugin, player.getLocation(), 5, 3);
			trap.setName(name);
			((TntTrap)trap).save(selector);
		}
		
		if(type.equals("phantom")) {
			trap = new PhantomTrap(plugin, player.getLocation(), 30);
			trap.setName(name);
			((PhantomTrap)trap).save(selector);
		}
		
		if(type.equals("pared")) {
			trap = new WallTrap(plugin, player.getLocation(), 10);
			trap.setName(name);
			((WallTrap)trap).save(selector);
		}
		
		if(type.equals("rayos")) {
			trap = new ThorTrap(plugin, player.getLocation(), 5, 5);
			trap.setName(name);
			((ThorTrap)trap).save(selector);
		}
		
		plugin.getManager().traps.put(name, trap);
		
		plugin.getUtils().sendMessage(s, "&aTrampa creada con exito.");
		plugin.getUtils().sendMessage(s, "&aPuedes obtener un boton activador con: /"+l+" boton <name>");
		return true;
	}

	private boolean itemCommand(CommandSender s, String l, String[] a) {
		if(!(s instanceof Player)) {
			plugin.getUtils().sendMessage(s, "&cEste comando es solo para jugadores.");
			return true;
		}
		Player player = (Player)s;
		
		ItemStack item = new ItemStack(Material.GOLDEN_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setLocalizedName("deathrun");
		meta.setDisplayName(plugin.getUtils().color("&eDeathRun selector"));
		meta.setLore(List.of("", "§7Selecciona 2 esquinas contrarias", "§7y pon /deathrun create <tipo> <nombre>"));
		item.setItemMeta(meta);
		
		player.getInventory().addItem(item);
		return true;
	}
}
