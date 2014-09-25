package com.pkadev.pkaadventure.objects;

import java.util.HashMap;
import java.util.List;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.types.MobType;

public class PKAMob extends PKALivingEntity {

	public PKAMob(String mobName, int[] attributes, double maxHealth, double damage, int level,
			MobStrength mobStrength, MobStance mobStance, MobType mobType, int rareItemInt, HashMap<Integer, Ability> abilities) {
		super(mobName, damage, level, attributes, maxHealth, abilities);
		setLevel(level);
		setMobStrength(mobStrength);
		setMobStance(mobStance);
		setMobType(mobType);
		setRareItemInt(rareItemInt);
	}
	
	public PKAMob(String mobName, int[] attributes, double maxHealth, double damage, int level,
			MobStrength mobStrength, MobStance mobStance, MobType mobType, int rareItemInt, List<String> questReferences) {
		super(mobName, damage, level, attributes, maxHealth, new HashMap<Integer, Ability>());
		setLevel(level);
		setMobStrength(mobStrength);
		setMobStance(mobStance);
		setMobType(mobType);
		setRareItemInt(rareItemInt);
		setQuestReferences(questReferences);
	}
	
	public PKAMob(String mobName, int[] attributes, double maxHealth, double damage, int level,
			MobStrength mobStrength, MobStance mobStance, MobType mobType, int rareItemInt, HashMap<Integer, Ability> abilities, List<String> questReferences) {
		super(mobName, damage, level, attributes, maxHealth, abilities);
		setLevel(level);
		setMobStrength(mobStrength);
		setMobStance(mobStance);
		setMobType(mobType);
		setRareItemInt(rareItemInt);
		setQuestReferences(questReferences);
	}
	
	private int level;
	private MobStrength mobStrength;
	private MobStance mobStance;
	private MobType mobType;
	private int rareItemInt;
	private List<String> questReferences;
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public MobStrength getMobStrength() {
		return mobStrength;
	}
	
	public void setMobStrength(MobStrength mobStrength) {
		this.mobStrength = mobStrength;
	}
	
	public MobStance getMobStance() {
		return mobStance;
	}
	
	public void setMobStance(MobStance mobStance) {
		this.mobStance = mobStance;
	}
	
	public MobType getMobType() {
		return mobType;
	}
	
	public void setMobType(MobType mobType) {
		this.mobType = mobType;
	}
	
	public int getRareItemInt() {
		return rareItemInt;
	}
	
	public void setRareItemInt(int rareItemInt) {
		this.rareItemInt = rareItemInt;
	}

	public List<String> getQuestReferences() {
		return questReferences;
	}

	public void setQuestReferences(List<String> questReferences) {
		this.questReferences = questReferences;
	}
	
	
}
