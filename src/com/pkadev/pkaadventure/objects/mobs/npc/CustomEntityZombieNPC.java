package com.pkadev.pkaadventure.objects.mobs.npc;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;

import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R4.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R4.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.World;

public class CustomEntityZombieNPC extends EntityVillager implements MobMonster {

	public CustomEntityZombieNPC(World world) {
		super(world);
	}

	@Override
	public void initiate(SpawnNode spawnNode) {
		this.setCustomName(spawnNode.getName());
		this.setCustomNameVisible(true);
		
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
		this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this,
				EntityPlayer.class, 12.0F, 1.0F));
	}

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
