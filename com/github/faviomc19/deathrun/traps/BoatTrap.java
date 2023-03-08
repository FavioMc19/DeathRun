package com.github.faviomc19.deathrun.traps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;

public class BoatTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	
	private List<Boat> boats = new ArrayList<Boat>();
	
	public BoatTrap(DeathRun plugin, Location location, int duration) {
		super(plugin, location);
		setCooldown(duration);
	}
	
	public void setPos1(Location pos1) {
		this.pos_1 = pos1;
	}
	
	public void setPos2(Location pos2) {
		this.pos_2 = pos2;
	}

	@Override
	public void initTrap() {
		if(this.isActived())
			return;
		
		this.setActived(true);
		for(Player player : pos_1.getWorld().getPlayers()) {
			if(!inArea(player) || !plugin.getManager().check(null, player))
				continue;
			
			Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation().add(0, 0.5, 0), EntityType.BOAT);
			boat.setCustomName("TRAP");
			boat.addPassenger(player);
			boats.add(boat);
		}
	}

	@Override
	public void resetTrap() {
		Iterator<Boat> iterator = boats.iterator();
		while(iterator.hasNext()) {
			Boat boat = iterator.next();
			boat.setCustomName("delete");
			boat.remove();
		}
		boats.clear();
	}

	@Override
	public void time() {
	}
	
	public boolean inArea(Player player) {
		double p_x = player.getLocation().getX();
		double p_y = player.getLocation().getY();
		double p_z = player.getLocation().getZ();
		
		boolean x = p_x >= pos_1.getX() && p_x <= pos_2.getX()+1;
		boolean y = p_y >= pos_1.getY() && p_y <= pos_2.getY()+1;
		boolean z = p_z >= pos_1.getZ() && p_z <= pos_2.getZ()+1;
		
		return x && y && z;
	}
	
	public void save() {
		NekoConfig config = plugin.getConfigManager().traps;
		
		config.set("traps."+name+".type", type());
		config.set("traps."+name+".pos1", pos_1);
		config.set("traps."+name+".pos2", pos_2);
		config.set("traps."+name+".duration", getCooldown());
		config.saveConfig();
	}
	
	public void save(Selector selector) {
		this.pos_1 = selector.getLocation(true);
		this.pos_2 = selector.getLocation(false);
		save();
	}

	@Override
	public String type() {
		return "boat";
	}

}
