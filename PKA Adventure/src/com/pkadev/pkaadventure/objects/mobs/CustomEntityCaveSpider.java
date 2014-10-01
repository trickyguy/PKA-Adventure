package com.pkadev.pkaadventure.objects.mobs;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.processors.MobProcessor;

import net.minecraft.server.v1_7_R4.EntityCaveSpider;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityZombie;
import net.minecraft.server.v1_7_R4.World;

public class CustomEntityCaveSpider extends EntityCaveSpider implements MobMonster {

	public CustomEntityCaveSpider(World world) {
		super(world);
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
	public void initiate(SpawnNode spawnNode) {
		MobProcessor.setCreaturePathfinders(this, spawnNode.getMobStance(), this.goalSelector, this.targetSelector);
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
