package com.pkadev.pkaadventure.objects;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.types.AbilityTriggerType;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;

public class PKAPlayer extends PKALivingEntity {

	public PKAPlayer(String playerName, ClassType classType, int maxHealth, 
			int health, double damage, int weaponSlot, int availableUpgradePoints, int miningExp, int miningLevel, int gold) {
		super(playerName, damage, maxHealth, health);
		setClassType(classType);
		setWeaponSlot(weaponSlot);
		setAvailableUpgradePoints(availableUpgradePoints);
		
		setMiningExp(miningExp);
		setMiningLevel(miningLevel);
		setGoldAmount(gold);
	}
	
	private ClassType classType;
	private HashMap<Integer, Ability> abilities = new HashMap<Integer, Ability>();
	private HashMap<Integer, ItemStack> cachedItems = new HashMap<Integer, ItemStack>();
	private boolean isSneaking = false;
	//there might be more than 1 way for players to select abilities
	// 1 = change the hotbar, 2 = shift + number
	private int abilitySelectionType = 1;
	private Integer currentlySelectedAbility = 9;
	private int weaponSlot;
	private int availableUpgradePoints;
	private int experience;
	private int noDamageTicksTaken = -1;
	private int noDamageTicksGiven = -1;
	
	private int goldAmount;
	// EXP, Level
	private int miningExp;
	private int miningLevel;
	
	public ClassType getClassType() {
		return classType;
	}
	public void setClassType(ClassType classType) {
		this.classType = classType;
	}
	public Inventory getAbilityInventory() {
		return InventoryUtil.fillInventory("ability", ItemUtil.getItemStacksFromAbilities(abilities));
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
	public int getAbiltiySelectionType() {
		return abilitySelectionType;
	}
	public void setAbilitySelectionType(int abilityTriggerType) {
		this.abilitySelectionType = abilityTriggerType;
	}
	public Integer getCurrentlySelectedAbility() {
		return currentlySelectedAbility;
	}
	/**
	 * @param currentlySelectedAbility
	 * @return abilityName
	 */
	public String setCurrentlySelectedAbility(Integer currentlySelectedAbility) {
		Ability ability = abilities.get(currentlySelectedAbility);
		if (ability == null) {
			if (abilities.get(this.currentlySelectedAbility) != null) {
				return abilities.get(this.currentlySelectedAbility).getName();
			} else {
				return "No";
			}
		}
		this.currentlySelectedAbility = currentlySelectedAbility;
		return abilities.get(currentlySelectedAbility).getName();
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
	public int getNoDamageTicksTaken() {
		return noDamageTicksTaken;
	}
	public void setNoDamageTicksTaken(int noDamageTicksTaken) {
		this.noDamageTicksTaken = noDamageTicksTaken;
	}
	public int getNoDamageTicksGiven() {
		return noDamageTicksGiven;
	}
	public void setNoDamageTicksGiven(int noDamageTicksGiven) {
		this.noDamageTicksGiven = noDamageTicksGiven;
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
	public void addGoldAmount(int gold) {
		goldAmount += gold;
	}
	public boolean removeGoldAmount(int gold) {
		if (goldAmount - gold < 0)
			return false;
		goldAmount -= gold;
		return true;
	}
}
