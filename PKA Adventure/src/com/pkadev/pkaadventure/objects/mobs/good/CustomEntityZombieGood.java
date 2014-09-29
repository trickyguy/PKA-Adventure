package com.pkadev.pkaadventure.objects.mobs.good;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityZombie;
import net.minecraft.server.v1_7_R4.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R4.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R4.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R4.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.World;

import com.pkadev.pkaadventure.interfaces.MobMonster;
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
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityCaveSpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityEndermanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityGolemNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityPigmanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySilverfishNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySkeletonNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityZombieNeutral;

public class CustomEntityZombieGood extends EntityZombie implements MobMonster {

	private SpawnNode node;
	private PKAMob pkaMob;
	
	@Override
	public SpawnNode getSpawnNode() {
		return node;
	}
	
	@Override
	public void setSpawnNode(SpawnNode node) {
		this.node = node;
	}
	
	public CustomEntityZombieGood(World world) {
		super(world);
	}
	
	@Override
	public void initiate(SpawnNode spawnNode) {
		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(2, new PathfinderGoalMoveTowardsRestriction(this,
				1.0D));
		this.setCustomName(spawnNode.getName());
		this.setCustomNameVisible(true);
		switch (spawnNode.getMobStance()) {
		default:
			this.setCustomName("Failed Entity");
			this.setCustomNameVisible(true);
			return;
		case EVIL:
			break;
		case GOOD:
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityCaveSpiderEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityEndermanEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityGolemEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityPigmanEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySilverfishEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySkeletonEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySpiderEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityZombieEvil.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityCaveSpiderNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityEndermanNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityGolemNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityPigmanNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySilverfishNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySkeletonNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntitySpiderNeutral.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityZombieNeutral.class, 0, true));
		case NEUTRAL:
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
			
			//EVIL
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityCaveSpiderEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityEndermanEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityGolemEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityPigmanEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySilverfishEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySkeletonEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySpiderEvil.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityZombieEvil.class, 1D, true));
			
			//NEUTRAL
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityCaveSpiderNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityEndermanNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityGolemNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityPigmanNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySilverfishNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySkeletonNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntitySpiderNeutral.class, 1D, true));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityZombieNeutral.class, 1D, true));
			
			break;
		}
	}
	
	@Override
	public PKAMob getPKAMob() {
		return pkaMob;
	}

	@Override
	public void setPKAMob(PKAMob pkaMob) {
		this.pkaMob = pkaMob;
	}
	
	@Override
	public EntityInsentient getEntity() {
		return this;
	}

}
