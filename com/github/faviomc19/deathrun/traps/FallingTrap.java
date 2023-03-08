package com.github.faviomc19.deathrun.traps;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;

public class FallingTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	
	private Map<Location, BlockData> block_cache = new HashMap<Location, BlockData>();
	
	public FallingTrap(DeathRun plugin, Location location, int duration) {
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
		
		Selector selector = new Selector(pos_1, pos_2);
		
		Location p1 = selector.getLocation(true);
		Location p2 = selector.getLocation(false);
		
		
		for(int x = p1.getBlockX(); x < p2.getBlockX(); x++) {
			for(int z = p1.getBlockZ(); z < p2.getBlockZ(); z++) {
				for(int y = p1.getBlockY(); y < p2.getBlockY(); y++) {
					Block block = new Location(p1.getWorld(), x, y, z).getBlock();
					if(block.getType().equals(Material.AIR))
						continue;
					
					block_cache.put(block.getLocation(), block.getBlockData());
					FallingBlock falling = p1.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
					falling.setDropItem(false);
					falling.setCustomName("deathrun:falling");
					block.setType(Material.AIR);
				}
			}
		}
	}

	@Override
	public void resetTrap() {
		for(Location location : block_cache.keySet()) {
			BlockData data = block_cache.get(location);
			location.getBlock().setBlockData(data);
		}
		block_cache.clear();
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
		return "falling";
	}

}
