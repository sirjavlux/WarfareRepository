package com.coding.sirjavlux.effectUtils;

import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;

public class EffectParticle {

	private DustOptions dustOp;
	private int count;
	private int delayTicks;
	private Location loc;
	private Particle particle;
	private double extra;
	private Material mat;
	private double red, green, blue;
	private UUID uuid;
	private double xOffset, yOffset, zOffset;
	private double note;
	private UUID identifier;
	
	public EffectParticle(Location loc, Particle particle, int delayTicks, UUID uuid) {
		this.loc = loc;
		this.particle = particle;
		this.delayTicks = delayTicks;
		this.uuid = uuid;
		this.red = 0;
		this.green = 0;
		this.blue = 0;
		this.mat = Material.AIR;
		this.count = 1;
		this.dustOp = new DustOptions(Color.fromBGR(0, 0, 0), 1);
		this.extra = 0;
		this.zOffset = 0;
		this.xOffset = 0;
		this.yOffset = 0;
		this.note = 0;
		this.identifier = UUID.randomUUID();
	}
	
	public DustOptions getDustOptions() { return dustOp; }
	public void setDustOptions(DustOptions dustOp) { this.dustOp = dustOp; }
	
	public int getCount() { return count; }
	public void setCount(int count) { this.count = count; }
	
	public int getDelay() { return delayTicks; }
	public void setDelay(int delayTicks) { this.delayTicks = delayTicks; }
	
	public Location getLocation() { return loc; }
	public void setLocation(Location loc) { this.loc = loc; }
	
	public Particle getParticle() { return particle; }
	public void setParticle(Particle particle) { this.particle = particle; }
	
	public double getExtra() { return extra; }
	public void setExtra(double extra) { this.extra = extra; }
	
	public Material getMaterial() { return mat; }
	public void setMaterial(Material mat) { this.mat = mat; }
	
	public double[] getRGB() { return new double[] {red, green, blue}; }
	public void setRGB(double red, double green, double blue) { this.green = green / 255D; this.red = red / 255D; this.blue = blue / 255D; }
	
	public boolean hasUUID() { return uuid == null ? false : true; }
	public UUID getUUID() { return uuid; }
	public void setUUID(UUID uuid) { this.uuid = uuid; }
	
	public double[] getOffset() { return new double[] {xOffset, yOffset, zOffset}; }
	public void setOffset(double xOffset, double yOffset, double zOffset) { this.xOffset = xOffset; this.yOffset = yOffset; this.zOffset = zOffset; }

	public double getNote() { return note; }
	public void setNote(double note) { this.note = note / 24D; }
	
	public UUID getIdentifier() { return identifier; }
}
