package com.pkadev.pkaadventure.interfaces;

import net.minecraft.server.v1_7_R3.EntityInsentient;

import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;

public interface MobMonster {

	public void TEMPinitiate(SpawnNode spawnNode);
	public SpawnNode getSpawnNode();
	public void setSpawnNode(SpawnNode node);
	public PKAMob getPKAMob();
	public void setPKAMob(PKAMob pkaMob);
	public EntityInsentient getEntity();
}
