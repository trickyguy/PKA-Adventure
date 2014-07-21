package com.pkadev.pkaadventure.objects.mobs;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_7_R1.util.UnsafeList;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;

import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityZombie;
import net.minecraft.server.v1_7_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R1.World;

public class CustomEntityZombieEvil extends EntityZombie implements MobMonster {

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
	
	public CustomEntityZombieEvil(World world) {
		super(world);
	}

	@Override
	public void TEMPinitiate(SpawnNode spawnNode) {
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
		case GOOD:
			break;
		case PASSIVE:
			break;
		case EVIL:
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, 0, true));
			this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, CustomEntityZombieGood.class, 0, true));
		case NEUTRAL:
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
			this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, CustomEntityZombieGood.class, 1D, true));
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityPlayer.class, 1.2D, true));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
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
