package com.coding.sirjavlux.utils;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public abstract class SkullUtils {

	public static ItemStack getCustomSkull(ItemStack item, String url) {
	       
	    if (item.getType().equals(Material.PLAYER_HEAD)) {
	    	if (url.isEmpty()) return item;
		       
		    SkullMeta headMeta = (SkullMeta) item.getItemMeta();
		    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		       
		    profile.getProperties().put("textures", new Property("textures", url));
		       
		    try {
	            Field profileField = headMeta.getClass().getDeclaredField("profile");
	            profileField.setAccessible(true);
	            profileField.set(headMeta, profile);
	           
	        } catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error) {
	            error.printStackTrace();
	        }
		    item.setItemMeta(headMeta);
	    }
        return item;
    }
	
	public static ItemStack getCustomSkull(String url) {
	       
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
	    if (url.isEmpty()) return head;
	       
	    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
	    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
	       
	    profile.getProperties().put("textures", new Property("textures", url));
	       
	    try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
           
        } catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
