package com.pkadev.pkaadventure.processors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.utils.DamageUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MathUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class MobProcessor {
	private static Main plugin = Main.instance;
	
	public static void removeAllLivingMobs() {
		for (Entity entity : plugin.world.getEntities()) {
			if (entity instanceof LivingEntity) {
				entity.remove();
			}
		}
	}
	
	public static void damageMobByEnvironment(LivingEntity livingEntity, double minecraftDamage) {
		if (livingEntity.getNoDamageTicks() > 10)
			return;
		
		if (minecraftDamage < 5d)
			return;
		else {
			MobMonster mobMonster = (MobMonster) livingEntity;
			PKAMob pkaMob = mobMonster.getPKAMob();
			double maxHealth = pkaMob.getMaxHealth();
			double finalDamage = DamageUtil.getFinalizedDamage(minecraftDamage, maxHealth);
			damageMob(mobMonster, pkaMob, livingEntity, finalDamage, "");
		}
	}
	
	public static void damageMobByEntity(LivingEntity livingEntity, double finalizedDamage, String damagerName) {
		MobMonster mobMonster = 	getMobMonster(livingEntity);
		PKAMob pkaMob = 			getMobMonster(livingEntity).getPKAMob();
		damageMob(mobMonster, pkaMob, livingEntity, finalizedDamage, damagerName);
	}
	
	private static void damageMob(MobMonster mobMonster, PKAMob pkaMob, LivingEntity livingEntity, double finalizedDamage, String damagerName) {
		if (finalizedDamage <= 0d)
			return;
		double finalHealth = pkaMob.getHealth() - finalizedDamage;
		pkaMob.addDamageDoneBy(damagerName, finalizedDamage);
		if (finalHealth > 0)
			damageMobNonLethal(mobMonster, pkaMob, finalHealth);
		else {
			damageMobLethal(livingEntity);
		}
	}
	
	private static void damageMobNonLethal(MobMonster mobMonster, PKAMob pkaMob, double newHealth) {
		pkaMob.setHealth(newHealth);
		updateHealth(mobMonster, pkaMob);
	}
	
	private static void damageMobLethal(LivingEntity livingEntity) {
		Bukkit.broadcastMessage("a");
		livingEntity.damage(100d);
	}
	
	public static void mobDeath(MobMonster mobMonster, Location location) {
		PKAMob pkaMob = mobMonster.getPKAMob();
		giveOutExperience(mobMonster, mobMonster.getPKAMob());
		SpawnNodeProcessor.removeMobFromNode(mobMonster.getSpawnNode(), mobMonster);
		Arrays.fill(mobMonster.getEntity().getEquipment(), null);
		pkaMob.getDamageDoneBy().remove("");
		HashMap<String, List<ItemStack>> drops = ItemUtil.getNewItemDrop(pkaMob.getDamageDoneBy().keySet(), pkaMob.getMobName(), pkaMob.getLevel(), pkaMob.getRareItemInt());
		for (String player : drops.keySet()) {
			for (ItemStack itemStack : drops.get(player)) {
				Item item = location.getWorld().dropItem(location, itemStack);
				ItemUtil.addDroppedItem(item, player);
			}
		}
	}
	
	private static void updateHealth(MobMonster mobMonster, PKAMob pkaMob) {
		mobMonster.getEntity().setCustomName("§c[" + (int) pkaMob.getHealth() + "/" + (int) pkaMob.getMaxHealth() + "] §6Lvl. " + pkaMob.getLevel());
	}
	
	private static void giveOutExperience(MobMonster mobMonster, PKAMob pkaMob) {
		double experience = MathUtil.getValue(pkaMob.getLevel(), "mob_experience");
		experience = addMobStrengthMultiplier(experience, pkaMob.getMobStrength());
		for (String damagerName : pkaMob.getRewardPercentages().keySet()) {
			if (damagerName == "")
				continue;
			PlayerProcessor.rewardExperience(damagerName, (int) (experience * pkaMob.getRewardPercentages().get(damagerName)));
		}
	}
	
	public static double addMobStrengthMultiplier(double value, MobStrength mobStrength) {
		double mult = 1d;
		switch (mobStrength) {
			case ANIMAL:{
				mult = 0.5d;
				break;
			}
			case MINION:{break;}
			case GUARD:{
				mult = 1.5d;
				break;
			}
			case CHAMPION:{
				mult = 3d;
				break;
			}
			case BOSS:{
				mult = 10d;
				break;
			}
			default:break;
		}
		return value * mult;
	}
	public static int addMobStrengthMultiplier(int value, MobStrength mobStrength) {
		double mult = 1d;
		double doubleValue = (double) value;
		switch (mobStrength) {
			case ANIMAL:{
				mult = 0.5d;
				break;
			}
			case MINION:{break;}
			case GUARD:{
				mult = 1.5d;
				break;
			}
			case CHAMPION:{
				mult = 3d;
				break;
			}
			case BOSS:{
				mult = 10d;
				break;
			}
			default:break;
		}
		return (int) (doubleValue * mult);
	}
	
	public static boolean isMobMonster(Entity entity) {
		return ((CraftEntity) entity).getHandle() instanceof MobMonster;
	}
	
	public static MobMonster getMobMonster(Entity entity) {
		return (MobMonster) ((CraftEntity) entity).getHandle();
	}
}
