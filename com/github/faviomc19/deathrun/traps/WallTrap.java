package com.github.faviomc19.deathrun.traps;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import com.github.faviomc19.deathrun.DeathRun;
import com.github.faviomc19.deathrun.objects.NekoConfig;
import com.github.faviomc19.deathrun.objects.Selector;


public class WallTrap extends BaseTrap{
	
	private Location pos_1;
	private Location pos_2;
	
	private Map<Location, BlockData> block_cache = new HashMap<Location, BlockData>();
	
	public WallTrap(DeathRun plugin, Location location, int duration) {
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
		
		int y = pos_2.getBlockY();
		
		for(int x = pos_1.getBlockX(); x < pos_2.getBlockX(); x++) {
			for(int z = pos_1.getBlockZ(); z < pos_2.getBlockZ(); z++) {
				Location location = new Location(pos_1.getWorld(), x, y, z);
				
				if((x > pos_1.getBlockX() && x < pos_2.getBlockX()-1) && (z > pos_1.getBlockZ() && z < pos_2.getBlockZ()-1))
					continue;
                 
				Block block = location.getBlock();
      			Block block2 = location.getBlock().getRelative(BlockFace.UP);
				block_cache.put(location, block.getBlockData());
				block_cache.put(block2.getLocation(), block2.getBlockData());
				block.setType(Material.QUARTZ_BLOCK);
				block2.setType(Material.PURPUR_SLAB);
			}
		}
	}

	@Override
	public void resetTrap() {
		for(Location location : block_cache.keySet()) {
			location.getBlock().setBlockData(block_cache.get(location));
			location.getBlock().getState().update();
		}
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
		return "wall";
	}
	
}
