package com.pkadev.pkaadventure.processors;

import java.util.Arrays;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.utils.DamageUtil;
import com.pkadev.pkaadventure.utils.MathUtil;

public class MobProcessor {

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
			damageMob(mobMonster, pkaMob, finalDamage, "");
		}
	}
	
	public static void damagePlayerByEntity(LivingEntity livingEntity, double damage, int[] attributesAttacker, String damagerName) {
		MobMonster mobMonster = (MobMonster) livingEntity;
		PKAMob pkaMob = mobMonster.getPKAMob();
		double finalDamage = DamageUtil.getFinalizedDamage(damage, attributesAttacker, pkaMob.getAttributes());
		damageMob(mobMonster, pkaMob, finalDamage, damagerName);
	}
	
	private static void damageMob(MobMonster mobMonster, PKAMob pkaMob, double damage, String damagerName) {
		if (damage <= 0d)
			return;
		double finalHealth = pkaMob.getHealth() - damage;
		pkaMob.addDamageDoneBy(damagerName, damage);
		if (finalHealth > 0)
			damageMobNonLethal(mobMonster, pkaMob, finalHealth);
		else {
			damageMobLethal(mobMonster);
		}
	}
	
	private static void damageMobNonLethal(MobMonster mobMonster, PKAMob pkaMob, double newHealth) {
		pkaMob.setHealth(newHealth);
		updateHealth(mobMonster, pkaMob);
	}
	
	private static void damageMobLethal(MobMonster mobMonster) {
		mobMonster.getEntity().setHealth(0f);
	}
	
	public static void mobDeath(MobMonster mobMonster) {
		giveOutExperience(mobMonster, mobMonster.getPKAMob());
		SpawnNodeProcessor.removeMobFromNode(mobMonster.getSpawnNode(), mobMonster);
		Arrays.fill(mobMonster.getEntity().getEquipment(), null);
	}
	
	private static void updateHealth(MobMonster mobMonster, PKAMob pkaMob) {
		mobMonster.getEntity().setCustomName("§c[" + pkaMob.getHealth() + "/" + pkaMob.getMaxHealth() + "] &6Lvl. " + pkaMob.getLevel());
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
		return (((CraftEntity) entity).getHandle() instanceof MobMonster);
	}
	
	public static MobMonster getMobMonster(Entity entity) {
		return (MobMonster) ((CraftEntity) entity).getHandle();
	}
}
