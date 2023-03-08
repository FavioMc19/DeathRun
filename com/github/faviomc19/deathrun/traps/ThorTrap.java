package com.github.faviomc19.deathrun.traps;

import org.bukkit.Location;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;

import io.netty.util.internal.ThreadLocalRandom;

public class ThorTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	private int thors_per_seconds;
	
	public ThorTrap(DeathRun plugin, Location location, int thors_per_seconds, int duration) {
		super(plugin, location);
		this.thors_per_seconds = thors_per_seconds;
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
	}

	@Override
	public void resetTrap() {
	}

	@Override
	public void time() {
		for(int i = 0; i < thors_per_seconds; i++) {
			double y = pos_1.getBlockY();
			double x = ThreadLocalRandom.current().nextDouble(pos_1.getX(), pos_2.getX());
			double z = ThreadLocalRandom.current().nextDouble(pos_1.getZ(), pos_2.getZ());
			
			pos_2.getWorld().strikeLightning(new Location(pos_2.getWorld(), x, y, z));
		}
	}
	
	public void save() {
		NekoConfig config = plugin.getConfigManager().traps;
		
		config.set("traps."+name+".type", type());
		config.set("traps."+name+".pos1", pos_1);
		config.set("traps."+name+".pos2", pos_2);
		config.set("traps."+name+".duration", getCooldown());
		config.set("traps."+name+".thors_per_seconds", thors_per_seconds);
		config.saveConfig();
	}
	
	public void save(Selector selector) {
		this.pos_1 = selector.getLocation(true);
		this.pos_2 = selector.getLocation(false);
		save();
	}

	@Override
	public String type() {
		return "thor";
	}
	
}
