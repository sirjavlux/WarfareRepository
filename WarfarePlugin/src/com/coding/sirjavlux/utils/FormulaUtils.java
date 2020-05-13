package com.coding.sirjavlux.utils;

import org.bukkit.Location;

public abstract class FormulaUtils {

	public static Location getRandomLocation(Location origin, double radius) {
		double u = Math.random();
		double v = Math.random();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = origin.getX() + (radius * Math.sin(phi) * Math.cos(theta));
		double y = origin.getY() + (radius * Math.sin(phi) * Math.sin(theta));
		double z = origin.getZ() + (radius * Math.cos(phi));
	    Location newLoc = new Location(origin.getWorld(), x, y, z);
	        
	    return newLoc;
	}
}
