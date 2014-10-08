package com.pkadev.pkaadventure.processors;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.EntityAgeable;
import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R4.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R4.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R4.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R4.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKALivingEntity;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityCaveSpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityEndermanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityGolemEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityPigmanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySilverfishEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySkeletonEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityZombieEvil;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityCaveSpiderGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityEndermanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityGolemGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityPigmanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySilverfishGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySkeletonGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySpiderGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityZombieGood;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityCaveSpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityEndermanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityGolemNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityPigmanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySilverfishNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySkeletonNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityZombieNeutral;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MathUtil;

public class MobProcessor {
	
	public static void setCreaturePathfinders(EntityCreature entity, MobStance mobStance, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
		
		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			AttributeInstance e = (AttributeInstance) field.get(entity.getNavigation());
			e.setValue(12);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		goalSelector.a(0, new PathfinderGoalFloat(entity));
		goalSelector.a(2, new PathfinderGoalMoveTowardsRestriction(entity, 1.0D));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(entity));
		goalSelector.a(7, new PathfinderGoalRandomStroll(entity, 1.0D));
		
		if (mobStance == MobStance.EVIL || mobStance == MobStance.NEUTRAL) {
			
			targetSelector.a(1, new PathfinderGoalHurtByTarget(entity, true));
			
			goalSelector.a(2, new PathfinderGoalMeleeAttack(entity, EntityPlayer.class, 1.2D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityCaveSpiderGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityEndermanGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityGolemGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityPigmanGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySilverfishGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySkeletonGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySpiderGood.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityZombieGood.class, 1D, true));
			
			if (mobStance == MobStance.EVIL) {
				
				targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(entity, EntityPlayer.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityCaveSpiderGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityEndermanGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityGolemGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityPigmanGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySilverfishGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySkeletonGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySpiderGood.class, 0, true));
				targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityZombieGood.class, 0, true));
				
			}
		} else if (mobStance == MobStance.GOOD) {
			
			targetSelector.a(1, new PathfinderGoalHurtByTarget(entity, true));
			
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityCaveSpiderEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityEndermanEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityGolemEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityPigmanEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySilverfishEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySkeletonEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySpiderEvil.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityZombieEvil.class, 1D, true));
			
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityCaveSpiderNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityEndermanNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityGolemNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityPigmanNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySilverfishNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySkeletonNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntitySpiderNeutral.class, 1D, true));
			goalSelector.a(4, new PathfinderGoalMeleeAttack(entity, CustomEntityZombieNeutral.class, 1D, true));
			
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityCaveSpiderEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityEndermanEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityGolemEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityPigmanEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySilverfishEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySkeletonEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySpiderEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityZombieEvil.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityCaveSpiderNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityEndermanNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityGolemNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityPigmanNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySilverfishNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySkeletonNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntitySpiderNeutral.class, 0, true));
			targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(entity, CustomEntityZombieNeutral.class, 0, true));
			
		} else {
			
			goalSelector.a(8, new PathfinderGoalRandomLookaround(entity));
			goalSelector.a(2, new PathfinderGoalLookAtPlayer(entity, EntityPlayer.class, 12.0F, 1.0F));
		}
		
	}
	
	public static void setNPCPathfinders(EntityAgeable entity, MobStance mobStance, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
		
		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			AttributeInstance e = (AttributeInstance) field.get(entity.getNavigation());
			e.setValue(4);
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		goalSelector.a(0, new PathfinderGoalFloat(entity));
		goalSelector.a(2, new PathfinderGoalMoveTowardsRestriction(entity, 1.0D));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(entity));
		goalSelector.a(7, new PathfinderGoalRandomStroll(entity, 1.0D));
		goalSelector.a(2, new PathfinderGoalLookAtPlayer(entity, EntityPlayer.class, 12.0F, 1.0F));
		
	}
	
	public static void removeAllLivingMobs() {
		for (org.bukkit.entity.Entity entity : ElementsUtil.getWorld().getEntities()) {
			if (entity instanceof LivingEntity) {
				entity.remove();
			}
		}
	}
	
	public static void mobDeath(MobMonster mobMonster, Location location) {
		PKAMob pkaMob = mobMonster.getPKAMob();
		
		giveOutExperience(mobMonster, mobMonster.getPKAMob());
		SpawnNodeProcessor.removeMobFromNode(mobMonster.getSpawnNode(), mobMonster);
		Arrays.fill(mobMonster.getEntity().getEquipment(), null);
		pkaMob.getDamageDoneBy().remove("");
		HashMap<String, List<ItemStack>> drops = ItemUtil.getNewItemDrop(pkaMob.getDamageDoneBy().keySet(), 
				pkaMob.getName(), 
				pkaMob.getLevel(), 
				pkaMob.getRareItemInt());
		for (String player : drops.keySet()) {
			if (pkaMob.getQuestReferences() != null && pkaMob.getQuestReferences().size() != 0)
				QuestProcessor.mobDeath(pkaMob, player);
			for (ItemStack itemStack : drops.get(player)) {
				Item item = location.getWorld().dropItem(location, itemStack);
				ItemUtil.addDroppedItem(item, player);
			}
		}
		mobMonster.setPKAMob(null);
	}
	
	public static void giveCreatureName(EntityInsentient entity, SpawnNode node) {
		String prefix = "§c";
		if (node.getMobStance() == MobStance.GOOD || node.getMobStance() == MobStance.PASSIVE)
			prefix = "§a";
		else if (node.getMobStance() == MobStance.NEUTRAL) {
			prefix = "§e";
		}
		entity.setCustomNameVisible(true);
		entity.setCustomName(prefix + node.getName() + " §5Lvl. " + node.getLevel());
	}
	
	public static void updateHealth(LivingEntity livingEntity, PKAMob pkaMob) {
		String prefix = "§c";
		if (pkaMob.getMobStance() == MobStance.GOOD || pkaMob.getMobStance() == MobStance.PASSIVE)
			prefix = "§a";
		else if (pkaMob.getMobStance() == MobStance.NEUTRAL) {
			prefix = "§e";
		}
		livingEntity.setCustomName(prefix + "[" + (int) pkaMob.getHealth() + "/" + (int) pkaMob.getMaxHealth() + "] §6Lvl. " + pkaMob.getLevel());
	}
	
	private static void giveOutExperience(MobMonster mobMonster, PKAMob pkaMob) {
		double experience = MathUtil.getValue(pkaMob.getLevel(), "mob_experience");
		experience = addMobStrengthMultiplier(experience, pkaMob.getMobStrength());
		for (String damagerName : pkaMob.getRewardPercentages().keySet()) {
			if (damagerName == "")
				continue;
			int playerExperience = (int) (experience * pkaMob.getRewardPercentages().get(damagerName));
			PlayerProcessor.rewardExperience(damagerName, playerExperience);
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
	
	public static boolean isMobMonster(org.bukkit.entity.Entity entity) {
		return ((CraftEntity) entity).getHandle() instanceof MobMonster;
	}
	
	public static MobMonster getMobMonster(org.bukkit.entity.Entity entity) {
		return (MobMonster) ((CraftEntity) entity).getHandle();
	}
}
