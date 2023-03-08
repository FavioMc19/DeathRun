package com.github.faviomc19.deathrun.traps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;

import io.netty.util.internal.ThreadLocalRandom;

public class ArrowTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	private int arrows_by_seconds;
	private List<Arrow> arrows;
	
	public ArrowTrap(DeathRun plugin, Location location, int arrows_by_seconds, int duration) {
		super(plugin, location);
		this.arrows_by_seconds = arrows_by_seconds;
		setCooldown(duration);
		arrows = new ArrayList<Arrow>();
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
	}

	@Override
	public void resetTrap() {
		Iterator<Arrow> iterator = arrows.iterator();
		while(iterator.hasNext()) {
			iterator.next().remove();
		}
		arrows.clear();
	}

	@Override
	public void time() {
		for(int i = 0; i < arrows_by_seconds; i++) {
			double y = pos_2.getBlockY()+5;
			double x = ThreadLocalRandom.current().nextDouble(pos_1.getX(), pos_2.getX());
			double z = ThreadLocalRandom.current().nextDouble(pos_1.getZ(), pos_2.getZ());
			
			Arrow arrow = (Arrow) pos_2.getWorld().spawnEntity(new Location(pos_2.getWorld(), x, y, z), EntityType.ARROW);
			arrow.setCustomName("deathrun:arrow");
			Vector speed = new Vector(0, ThreadLocalRandom.current().nextDouble(-10, -0.3), 0);
			arrow.setVelocity(speed);
			arrows.add(arrow);
		}
	}
	
	public void save() {
		NekoConfig config = plugin.getConfigManager().traps;
		
		config.set("traps."+name+".type", type());
		config.set("traps."+name+".pos1", pos_1);
		config.set("traps."+name+".pos2", pos_2);
		config.set("traps."+name+".duration", getCooldown());
		config.set("traps."+name+".arrows_by_seconds", arrows_by_seconds);
		config.saveConfig();
	}
	
	public void save(Selector selector) {
		this.pos_1 = selector.getLocation(true);
		this.pos_2 = selector.getLocation(false);
		save();
	}

	@Override
	public String type() {
		return "arrow";
	}
	
}
