package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.processors.QuestProcessor;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class PKAPlayer extends PKALivingEntity {

	public PKAPlayer(Player player, ClassType classType, int level, int experience, int maxHealth, 
			int health, double damage, int weaponSlot, int availableUpgradePoints, int miningExp, int miningLevel, int gold,
			List<String> discoveredLocations) {
		super(player.getName(), level, damage, maxHealth, health);
		setExperience(experience);
		setPlayer(player);
		setClassType(classType);
		setWeaponSlot(weaponSlot);
		setAvailableUpgradePoints(availableUpgradePoints);
		setDiscoveredLocations(discoveredLocations);
		
		setMiningExp(miningExp); // miningExp
		setMiningLevel(miningLevel); // miningLevel
		setGoldAmount(gold);
	}
	
	private Player player;
	private ClassType classType;
	private int experience;
	private int experienceRequired;
	private HashMap<Integer, ItemStack> cachedItems = new HashMap<Integer, ItemStack>();
	private boolean isSneaking = false;
	//there might be more than 1 way for players to select abilities
	// 1 = change the hotbar, 2 = shift + number
	private int abilitySelectionType = 1;
	private Integer currentlySelectedAbility = 9;
	private int weaponSlot;
	private int availableUpgradePoints;
	private int noDamageTicksTaken = -1;
	private int noDamageTicksGiven = -1;
	private List<String> discoveredLocations = new ArrayList<String>();
	private HashMap<String, PKAQuest> activeQuests = new HashMap<String, PKAQuest>();
	private List<String> finishedQuests = new ArrayList<String>();
	
	private int goldAmount;
	
	
	//TODO EXPERIMENTAL
	private int cachedGoldAmount = 0;
	private boolean hasGoldTask = false;
	
	
	
	// EXP, Level
	private int miningExp;
	private int miningLevel;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public ClassType getClassType() {
		return classType;
	}
	public void setClassType(ClassType classType) {
		this.classType = classType;
	}
	public Inventory getAbilityInventory() {
		return InventoryUtil.fillInventory("ability", ItemUtil.getItemStacksFromAbilities(getAbilities()));
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
		Ability ability = getAbilities().get(currentlySelectedAbility);
		if (ability == null) {
			if (getAbilities().get(this.currentlySelectedAbility) != null) {
				return getAbilities().get(this.currentlySelectedAbility).getName();
			} else {
				return "No";
			}
		}
		this.currentlySelectedAbility = currentlySelectedAbility;
		return getAbilities().get(currentlySelectedAbility).getName();
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
	public void addExperience(int experience) {
		experience += experience;
	}
	public int getExperienceRequired() {
		return experienceRequired;
	}
	public void setExperienceRequired(int experienceRequired) {
		this.experienceRequired = experienceRequired;
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
	public List<String> getDiscoveredLocations() {
		return discoveredLocations;
	}
	public void discoverLocation(String locationName) {
		discoveredLocations.add(locationName);
	}
	public void setDiscoveredLocations(List<String> discoveredLocations) {
		this.discoveredLocations = discoveredLocations;
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
		if (!hasGoldTask) {
			cachedGoldAmount = gold;
			hasGoldTask = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {

				@Override
				public void run() {
					MessageUtil.sendMessage(player, "§6+" + cachedGoldAmount + " (" + goldAmount + ")", MessageType.SIMPLE);
					hasGoldTask = false;
				}
				
			}, 20l);
		} else {
			cachedGoldAmount += gold;
		}
		goldAmount += gold;
	}
	public boolean removeGoldAmount(int gold) {
		if (goldAmount - gold < 0)
			return false;
		goldAmount -= gold;
		return true;
	}

	public HashMap<String, PKAQuest> getActiveQuests() {
		return activeQuests;
	}

	public void addActiveQuest(String questReference, PKAQuest pkaQuest) {
		activeQuests.put(questReference, pkaQuest);
	}
	
	public void removeActiveQuest(String questReference) {
		activeQuests.remove(questReference);
	}
	
	public void setActiveQuests(HashMap<String, PKAQuest> activeQuests) {
		this.activeQuests = activeQuests;
	}

	public List<String> getFinishedQuests() {
		return finishedQuests;
	}
	
	public void addFinishedQuest(String questReference) {
		finishedQuests.add(questReference);
	}
	
	public void setFinishedQuests(List<String> finishedQuests) {
		this.finishedQuests = finishedQuests;
	}
	
}
