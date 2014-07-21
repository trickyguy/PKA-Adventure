package com.pkadev.pkaadventure.types;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R1.BiomeBase;
import net.minecraft.server.v1_7_R1.BiomeMeta;
import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.EntityTypes;
import net.minecraft.server.v1_7_R1.EntityZombie;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieEvil;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieGood;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieNeutral;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombiePassive;
import com.pkadev.pkaadventure.utils.FileUtil;

public enum CustomEntityType {

	ZombieEvil("ZombieEvil", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombieEvil.class), 
	ZombieGood("ZombieGood", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombieGood.class), 
	ZombiePassive("ZombiePassive", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombiePassive.class), 
	ZombieNeutral("ZombieNeutral", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombieNeutral.class);
	
	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	public Class<? extends EntityInsentient> customClass;

	private CustomEntityType(String name, int id, EntityType entityType,
			Class<? extends EntityInsentient> nmsClass,
			Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public Class<? extends EntityInsentient> getNMSClass() {
		return nmsClass;
	}

	public Class<? extends EntityInsentient> getCustomClass() {
		return customClass;
	}

	public static void load() {
		for (CustomEntityType entity : values())
			a(entity.getCustomClass(), entity.getName(), entity.getID());

		// BiomeBase#biomes became private.
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		} catch (Exception exc) {
			// Unable to fetch.
			return;
		}
		for (BiomeBase biomeBase : biomes) {
			if (biomeBase == null)
				break;

			// This changed names from J, K, L and M.
			for (String field : new String[] { "as", "at", "au", "av" })
				try {
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> mobList = (List<BiomeMeta>) list
							.get(biomeBase);

					// Write in our custom class.
					for (BiomeMeta meta : mobList)
						for (CustomEntityType entity : values())
							if (entity.getNMSClass().equals(meta.b))
								meta.b = entity.getCustomClass();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public static void unload() {
		for (CustomEntityType entity : values()) {
			// Remove our class references.
			try {
				((Map) getPrivateStatic(EntityTypes.class, "d")).remove(entity
						.getCustomClass());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				((Map) getPrivateStatic(EntityTypes.class, "f")).remove(entity
						.getCustomClass());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (CustomEntityType entity : values())
			try {
				// Unregister each entity by writing the NMS back in place of
				// the custom class.
				a(entity.getNMSClass(), entity.getName(), entity.getID());
			} catch (Exception e) {
				e.printStackTrace();
			}

		// Biomes#biomes was made private so use reflection to get it.
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		} catch (Exception exc) {
			// Unable to fetch.
			return;
		}
		for (BiomeBase biomeBase : biomes) {
			if (biomeBase == null)
				break;

			// The list fields changed names but update the meta regardless.
			for (String field : new String[] { "as", "at", "au", "av" })
				try {
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> mobList = (List<BiomeMeta>) list
							.get(biomeBase);

					// Make sure the NMS class is written back over our custom
					// class.
					for (BiomeMeta meta : mobList)
						for (CustomEntityType entity : values())
							if (entity.getCustomClass().equals(meta.b))
								meta.b = entity.getNMSClass();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		for (Entity entity : Bukkit.getWorld(FileUtil.getStringValueFromConfig("homeworld")).getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof Player)) {
				entity.remove();
			}
		}
	}

	private static Object getPrivateStatic(Class clazz, String f)
			throws Exception {
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	private static void a(Class paramClass, String paramString, int paramInt) {
		try {
			((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass,
					paramString);
			((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass,
					Integer.valueOf(paramInt));
		} catch (Exception exc) {
			// Unable to register the new class.
		}
	}

}
