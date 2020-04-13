package com.coding.sirjavlux.projectiles;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.World;

public class CustomEntitySnowballRegistry {

    private CustomEntitySnowballRegistry() {
    }

    public static void registerCustomEntities() throws NoSuchFieldException, IllegalAccessException {
        registerEntity("FiredProjectile", 14, Projectile.class);
    }
    public static void spawnEntity(Entity entity, World world) {
        world.addEntity(entity);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static void registerEntity(String name, int id, Class<? extends Entity> customClass) throws NoSuchFieldException, IllegalAccessException { 
        ((Map) getStaticField("c", net.minecraft.server.v1_15_R1.EntityTypes.class)).put(name, customClass);
        ((Map) getStaticField("d", net.minecraft.server.v1_15_R1.EntityTypes.class)).put(customClass, name);
        ((Map) getStaticField("f", net.minecraft.server.v1_15_R1.EntityTypes.class)).put(customClass, Integer.valueOf(id));
    }


    @SuppressWarnings("rawtypes")
	public static Object getStaticField(String fieldName, Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }


}
