package com.pkadev.pkaadventure.objects;

import org.bukkit.inventory.Inventory;

import com.pkadev.pkaadventure.types.ClassType;

public class PKAPlayer {

	public PKAPlayer(String playerName, ClassType classType, int maxHealth, 
			int health, int[] attributes, double damage, 
			Inventory abilityInventory, int weaponSlot, int availableUpgradePoints, int miningExp, int miningLevel) {
		setPlayerName(playerName);
		setClassType(classType);
		setMaxHealth(maxHealth);
		setHealth(health);
		setAttributes(attributes);
		setAbilityInventory(abilityInventory);
		setDamage(damage);
		setWeaponSlot(weaponSlot);
		setAvailableUpgradePoints(availableUpgradePoints);
		
		setMiningExp(miningExp);
		setMiningLevel(miningLevel);
		
	}
	
	private String playerName;
	private ClassType classType;
	private int[] attributes = new int[]{0, 0, 0, 0};
	private double maxHealth;
	private double health;
	private double damage;
	private Inventory abilityInventory;
	private int weaponSlot;
	private int availableUpgradePoints;
	private int experience;
	
	// EXP, Level
	private int miningExp;
	private int miningLevel;
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public ClassType getClassType() {
		return classType;
	}
	public void setClassType(ClassType classType) {
		this.classType = classType;
	}
	public int[] getAttributes() {
		return attributes;
	}
	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}
	public void addAttributes(int[] attributes) {
		for (int i = 0; i < 4; i++) {
			this.attributes[i] += attributes[i];
		}
	}
	public void removeAttributes(int[] attributes) {
		for (int i = 0; i < 4; i++) {
			this.attributes[i] -= attributes[i];
		}
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
	public void removeHealth(double damage) {
		this.health -= damage;
	}
	public Inventory getAbilityInventory() {
		return abilityInventory;
	}
	public void setAbilityInventory(Inventory abilityInventory) {
		this.abilityInventory = abilityInventory;
	}
	public double getDamage() {
		return damage;
	}
	public void setDamage(double damage) {
		this.damage = damage;
	}
	public int getWeaponSlot() {
		return weaponSlot;
	}
	public void setWeaponSlot(int weaponSlot) {
		this.weaponSlot = weaponSlot;
	}
	public int getAvailableUpgradePoints() {
		return availableUpgradePoints;
	}
	public void setAvailableUpgradePoints(int availableUpgradePoints) {
		this.availableUpgradePoints = availableUpgradePoints;
	}
	public void addAvailableUpgradePoint() {
		availableUpgradePoints += 1;
	}
	public void removeAvailableUpgradePoint() {
		availableUpgradePoints -= 1;
	}
	public int getExperience() {
		return experience;
	}
	
	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public int getMiningExp() {
		return miningExp;
	}
	
	public void setMiningExp(int experience) {
		this.miningExp = experience;
	}
	
	public int getMiningLevel() {
		return miningLevel;
	}
	
	public void setMiningLevel(int level) {
		this.miningLevel = level;
	}
}
