package com.github.faviomc19.deathrun.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class Selector {
	private Location pos1;
	private Location pos2;
	
	public Selector() {
	}
	
	public Selector(Location pos1, Location pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
	}
	
	public void setPos1(Location location) {
		pos1 = location;
	}
	
	public void setPos2(Location location) {
		pos2 = location;
	}
	
	public Location getPos1() {
		return pos1;
	}
	
	public Location getPos2() {
		return pos2;
	}
	
	public void draw() {
		if(this.pos1 == null || this.pos2 == null)
			return;
		
		if(this.pos1.distance(this.pos2) >= 150)
			return;
		
		if(this.pos1.equals(this.pos2))
			return;
		
		for(Location location : getHollowCube(0.25D)) {
			pos1.getWorld().spawnParticle(Particle.DRIPPING_OBSIDIAN_TEAR, location, 1);
		}
	}
	
	public Location getLocation(Boolean min) {
		double xmin = Math.min(pos1.getX(), pos2.getX());
		double ymin = Math.min(pos1.getY(), pos2.getY());
		double zmin = Math.min(pos1.getZ(), pos2.getZ());
		
		double xmax = Math.max(pos1.getX(), pos2.getX())+1;
		double ymax = Math.max(pos1.getY(), pos2.getY())+1;
		double zmax = Math.max(pos1.getZ(), pos2.getZ())+1;
		
		if(min)
			return new Location(pos1.getWorld(), xmin, ymin, zmin);
		
		return new Location(pos1.getWorld(), xmax, ymax, zmax);
	}
	
	public List<Location> getHollowCube(double particleDistance) {
        List<Location> result = new ArrayList<Location>();
        World world = pos1.getWorld();
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX())+1;
        double maxY = Math.max(pos1.getY(), pos2.getY())+1;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ())+1;
     
        for (double x = minX; x <= maxX; x+=particleDistance) {
            for (double y = minY; y <= maxY; y+=particleDistance) {
                for (double z = minZ; z <= maxZ; z+=particleDistance) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
     
        return result;
    }
	
	public Boolean inZone(Location location) {
		
		double xmin = Math.min(pos1.getX(), pos2.getX());
		double ymin = Math.min(pos1.getY(), pos2.getY());
		double zmin = Math.min(pos1.getZ(), pos2.getZ());
		
		double xmax = Math.max(pos1.getX(), pos2.getX()+1);
		double ymax = Math.max(pos1.getY(), pos2.getY()+1);
		double zmax = Math.max(pos1.getZ(), pos2.getZ()+1);
		
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		return x >= xmin && x <= xmax && y >= ymin && y <= ymax && z >= zmin && z <= zmax;
	}
}
