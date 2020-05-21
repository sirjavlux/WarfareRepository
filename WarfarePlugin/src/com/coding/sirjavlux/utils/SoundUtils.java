package com.coding.sirjavlux.utils;

import org.bukkit.Sound;
import org.bukkit.block.Block;

public class SoundUtils {

	public static Sound getBlockBreakSound(Block block) {
		
		Sound sound = Sound.BLOCK_STONE_BREAK;
		String name = block.getType().name();
		if (name.contains("LOG") || name.contains("PLANK") || name.contains("COAL") || name.contains("CHEST") || (name.contains("DOOR") && !name.contains("IRON"))) sound = Sound.BLOCK_WOOD_BREAK;
		else if (name.contains("LEAVES") || name.contains("GRASS") || name.contains("FLOWER") || name.contains("SAPLING") || name.contains("DIRT") || name.contains("GRAVEL")) sound = Sound.BLOCK_GRASS_BREAK;
		else if (name.contains("STONE")) sound = Sound.BLOCK_STONE_BREAK;
		else if (name.contains("SAND")) sound = Sound.BLOCK_SAND_BREAK;
		else if (name.contains("GLASS")) sound = Sound.BLOCK_GLASS_BREAK;
		else if (name.contains("DIAMOND") 
				|| name.contains("IRON")
				|| name.contains("GOLD") 
				|| name.contains("EMERALD")) sound = Sound.BLOCK_METAL_BREAK; 
		else {
			try { sound = Sound.valueOf("BLOCK_" + block.getType().name().toUpperCase() + "_BREAK"); } catch(Exception e) { }
		}
		return sound;
	}
}
