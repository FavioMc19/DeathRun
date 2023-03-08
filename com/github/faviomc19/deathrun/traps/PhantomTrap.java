package com.github.faviomc19.deathrun.traps;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;

import io.netty.util.internal.ThreadLocalRandom;

public class PhantomTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	private Phantom phantom;
	
	public PhantomTrap(DeathRun plugin, Location location, int duration) {
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
		double y = pos_2.getBlockY()+8;
		double x = ThreadLocalRandom.current().nextDouble(pos_1.getX(), pos_2.getX());
		double z = ThreadLocalRandom.current().nextDouble(pos_1.getZ(), pos_2.getZ());
			
		phantom = (Phantom) pos_2.getWorld().spawnEntity(new Location(pos_2.getWorld(), x, y, z), EntityType.PHANTOM);
		phantom.setSize(30);
		phantom.setCustomName("deathrun:phantom");
		phantom.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(phantom.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()+7);
		phantom.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 1000, 2, false));
		Player target = null;
		
		for(Player player : phantom.getWorld().getPlayers()) {
			if(!plugin.getManager().check(null, player))
				continue;
			
			if(target == null)
				target = player;
			
			if(target != null && player.getLocation().distance(phantom.getLocation()) < target.getLocation().distance(phantom.getLocation()))
				target = player;
		}
		if(target != null && target.getLocation().distance(phantom.getLocation()) <= 50)
			phantom.setTarget(target);;
	}

	@Override
	public void resetTrap() {
		phantom.remove();
	}

	@Override
	public void time() {
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
		return "phantom";
	}
	
}
