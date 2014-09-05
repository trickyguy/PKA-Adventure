package com.pkadev.pkaadventure.objects;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class PKAPlayer {

	public PKAPlayer(String playerName, ClassType classType, int maxHealth, 
			int health, int[] attributes, double damage, int weaponSlot, int availableUpgradePoints, int miningExp, int miningLevel, int gold) {
		setPlayerName(playerName);
		setClassType(classType);
		setMaxHealth(maxHealth);
		setHealth(health);
		setAttributes(attributes);
		setDamage(damage);
		setWeaponSlot(weaponSlot);
		setAvailableUpgradePoints(availableUpgradePoints);
		
		setMiningExp(miningExp);
		setMiningLevel(miningLevel);
		setGoldAmount(gold);
	}
	
	private String playerName;
	private ClassType classType;
	private int[] attributes = new int[]{0, 0, 0, 0};
	private double maxHealth;
	private double health;
	private double damage;
	private HashMap<Integer, Ability> abilities = new HashMap<Integer, Ability>();
	private HashMap<Integer, ItemStack> cachedItems = new HashMap<Integer, ItemStack>();
	private boolean isSneaking = false;
	//there might be more than 1 way for players to select abilities
	// 1 = change the hotbar, 2 = shift + number
	private int abilityTriggerType = 1;
	private int weaponSlot;
	private int availableUpgradePoints;
	private int experience;
	
	private int goldAmount;
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
		return InventoryUtil.fillInventory("ability", ItemUtil.getItemStacksFromAbilities(abilities));
	}
	public HashMap<Integer, Ability> getAbilities() {
		return abilities;
	}
	public Ability getAbility(Integer i) {
		return abilities.get(i);
	}
	public void setAbility(Integer i, Ability ability) {
		abilities.put(i, ability);
	}
	public HashMap<Integer, ItemStack> getCachedItems() {
		return cachedItems;
	}
	public void setCachedItems(HashMap<Integer, ItemStack> cachedItems) {
		this.cachedItems = cachedItems;
	}
	public boolean isSneaking() {
		return isSneaking;
	}
	public void setIsSneaking(boolean isSneaking) {
		this.isSneaking = isSneaking;
	}
	public void setSneaking() {
		isSneaking = true;
	}
	public void setNotSneaking() {
		isSneaking = false;
	}
	public int getAbiltiyTriggerType() {
		return abilityTriggerType;
	}
	public void setAbilityTriggerType(int abilityTriggerType) {
		this.abilityTriggerType = abilityTriggerType;
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
	
	public int getGoldAmount() {
		return goldAmount;
	}
	
	public void setGoldAmount(int gold) {
		this.goldAmount = gold;
	}
}
