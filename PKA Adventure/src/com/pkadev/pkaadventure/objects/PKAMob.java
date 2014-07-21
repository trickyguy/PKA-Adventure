package com.pkadev.pkaadventure.objects;

import java.util.HashMap;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.types.MobType;

public class PKAMob {

	public PKAMob(String mobName, int[] attributes, double maxHealth, double damage, int level,
			MobStrength mobStrength, MobStance mobStance, MobType mobType, int rareItemInt) {
		damageDoneBy.put("", 0d);
		setMobName(mobName);
		setAttributes(attributes);
		setMaxHealth(maxHealth);
		setHealth(maxHealth);
		setDamage(damage);
		setLevel(level);
		setMobStrength(mobStrength);
		setMobStance(mobStance);
		setMobType(mobType);
		setRareItemInt(rareItemInt);
	}
	
	private String mobName;
	private int[] attributes = new int[]{0, 0, 0, 0};
	private double maxHealth;
	private double health;
	private double damage;
	private int level;
	private MobStrength mobStrength;
	private MobStance mobStance;
	private MobType mobType;
	private HashMap<String, Double> damageDoneBy = new HashMap<String, Double>();
	private double totalDamageReceived;
	private int rareItemInt;
	
	public String getMobName() {
		return mobName;
	}
	public void setMobName(String mobName) {
		this.mobName = mobName;
	}
	public int[] getAttributes() {
		return attributes;
	}
	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}
	public double getMaxHealth() {
		return maxHealth;
	}
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}
	public double getHealth() {
		return health;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public double getDamage() {
		return damage;
	}
	public void setDamage(double damage) {
		this.damage = damage;
	}
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
	public HashMap<String, Double> getDamageDoneBy() {
		return damageDoneBy;
	}
	public void setDamageDoneBy(HashMap<String, Double> damageDoneBy) {
		this.damageDoneBy = damageDoneBy;
	}
	public int getRareItemInt() {
		return rareItemInt;
	}
	public void setRareItemInt(int rareItemInt) {
		this.rareItemInt = rareItemInt;
	}
	
	/**
	 * gives you a list of all the players that have done damage, and gives them a value of 0-1 depending on what percent of the mob they killed
	 * INTERNAL: note, it does not return the damageDoneBy list, it makes sure to calculate the percentages
	 * @return
	 */
	public HashMap<String, Double> getRewardPercentages() {
		HashMap<String, Double> rewardPercents = new HashMap<String, Double>();
		for (String playerName : damageDoneBy.keySet()) {
			if (playerName == "")
				continue;
			double rewardPercent = damageDoneBy.get(playerName) / totalDamageReceived;
			if (rewardPercent < 0.1)
				rewardPercent = 0.1;
			else if(rewardPercent > 1)
				rewardPercent = 1;
			rewardPercents.put(playerName, Double.valueOf(rewardPercent));
		}
		return rewardPercents;
	}
	
	public void addDamageDoneBy(String playerName, double damage) {
		if (damageDoneBy.containsKey(playerName))
			damage = damageDoneBy.get(playerName) + damage;
		damageDoneBy.put(playerName, damage);
	}
	
}
