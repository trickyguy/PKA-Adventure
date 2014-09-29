package com.pkadev.pkaadventure.types;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R4.BiomeBase;
import net.minecraft.server.v1_7_R4.BiomeMeta;
import net.minecraft.server.v1_7_R4.EntityCaveSpider;
import net.minecraft.server.v1_7_R4.EntityEnderman;
import net.minecraft.server.v1_7_R4.EntityGolem;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityPigZombie;
import net.minecraft.server.v1_7_R4.EntitySilverfish;
import net.minecraft.server.v1_7_R4.EntitySkeleton;
import net.minecraft.server.v1_7_R4.EntitySpider;
import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.EntityWitherSkull;
import net.minecraft.server.v1_7_R4.EntityZombie;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityCaveSpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityEndermanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityGolemEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityPigmanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySilverfishEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySkeletonEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityWitherSkullEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityZombieEvil;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityEndermanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityGolemGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityPigmanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySilverfishGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySkeletonGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySpiderGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityWitherSkullGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityZombieGood;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityEndermanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityGolemNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityPigmanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySilverfishNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySkeletonNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityWitherSkullNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityZombieNeutral;
import com.pkadev.pkaadventure.objects.mobs.npc.CustomEntityVillagerNPC;
import com.pkadev.pkaadventure.objects.mobs.npc.CustomEntityZombieNPC;
import com.pkadev.pkaadventure.utils.FileUtil;

public enum CustomEntityType {

	//EVIL
	ZombieEvil(			"ZombieEvil", 			54, EntityType.ZOMBIE, 		EntityZombie.class, 	CustomEntityZombieEvil.class),
	GolemEvil(			"GolemEvil", 			99, EntityType.IRON_GOLEM, 	EntityGolem.class, 		CustomEntityGolemEvil.class),
	PigmanEvil(			"PigmanEvil", 			57, EntityType.PIG_ZOMBIE, 	EntityPigZombie.class, 	CustomEntityPigmanEvil.class),
	SilverfishEvil(		"SilverfishEvil", 		60, EntityType.SILVERFISH, 	EntitySilverfish.class, CustomEntitySilverfishEvil.class),
	SkeletonEvil(		"SkeletonEvil", 		51, EntityType.SKELETON, 	EntitySkeleton.class, 	CustomEntitySkeletonEvil.class),
	SpiderEvil(			"SpiderEvil", 			52, EntityType.SPIDER, 		EntitySpider.class, 	CustomEntitySpiderEvil.class),
	EndermanEvil(		"EndermanEvil", 		58, EntityType.ENDERMAN, 	EntityEnderman.class, 	CustomEntityEndermanEvil.class),
	CaveSpiderEvil(		"CaveSpiderEvil", 		59, EntityType.CAVE_SPIDER, EntityCaveSpider.class, CustomEntityCaveSpiderEvil.class),
	
	//GOOD
	ZombieGood(			"ZombieGood", 			54, EntityType.ZOMBIE, 		EntityZombie.class, 	CustomEntityZombieGood.class),
	GolemGood(			"GolemGood", 			99, EntityType.IRON_GOLEM, 	EntityGolem.class, 		CustomEntityGolemGood.class),
	PigmanGood(			"PigmanGood", 			57, EntityType.PIG_ZOMBIE, 	EntityPigZombie.class, 	CustomEntityPigmanGood.class),
	SilverfishGood(		"SilverfishGood", 		60, EntityType.SILVERFISH, 	EntitySilverfish.class, CustomEntitySilverfishGood.class),
	SkeletonGood(		"SkeletonGood", 		51, EntityType.SKELETON, 	EntitySkeleton.class, 	CustomEntitySkeletonGood.class),
	SpiderGood(			"SpiderGood", 			52, EntityType.SPIDER, 		EntitySpider.class, 	CustomEntitySpiderGood.class),
	EndermanGood(		"EndermanGood", 		58, EntityType.ENDERMAN, 	EntityEnderman.class, 	CustomEntityEndermanGood.class),
	
	//NEUTRAL
	ZombieNeutral(		"ZombieNeutral", 		54, EntityType.ZOMBIE, 		EntityZombie.class, 	CustomEntityZombieNeutral.class),
	GolemNeutral(		"GolemNeutral", 		99, EntityType.IRON_GOLEM, 	EntityGolem.class, 		CustomEntityGolemNeutral.class),
	PigmanNeutral(		"PigmanNeutral", 		57, EntityType.PIG_ZOMBIE, 	EntityPigZombie.class, 	CustomEntityPigmanNeutral.class),
	SilverfishNeutral(	"SilverfishNeutral",	60, EntityType.SILVERFISH, 	EntitySilverfish.class, CustomEntitySilverfishNeutral.class),
	SkeletonNeutral(	"SkeletonNeutral", 		51, EntityType.SKELETON, 	EntitySkeleton.class,	CustomEntitySkeletonNeutral.class),
	SpiderNeutral(		"SpiderNeutral", 		52, EntityType.SPIDER, 		EntitySpider.class, 	CustomEntitySpiderNeutral.class),
	EndermanNeutral(	"EndermanNeutral", 		58, EntityType.ENDERMAN, 	EntityEnderman.class, 	CustomEntityEndermanNeutral.class),
	
	//NPC
	VillagerNPC("VillagerNPC", 120, EntityType.VILLAGER, EntityVillager.class, CustomEntityVillagerNPC.class),
	ZombieNPC("ZombieNPC", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombieNPC.class);
	
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

	@SuppressWarnings("rawtypes")
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
		for (Entity entity : Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml")).getEntities()) {
			if ((entity instanceof LivingEntity && !(entity instanceof Player)) || (entity instanceof Item)) {
				entity.remove();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static Object getPrivateStatic(Class clazz, String f)
			throws Exception {
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
