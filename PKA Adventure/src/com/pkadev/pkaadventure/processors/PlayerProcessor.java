package com.pkadev.pkaadventure.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.DamageUtil;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MathUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class PlayerProcessor {
	private static Main plugin = Main.instance;
	private static HashMap<String, PKAPlayer> pkaPlayers = new HashMap<String, PKAPlayer>();

	public static PKAPlayer getPKAPlayer(String playerName) {
		return pkaPlayers.get(playerName);
	}
	public static PKAPlayer getPKAPlayer(Player player) {
		return getPKAPlayer(player.getName());
	}
	private static void addPKAPlayer(String playerName, PKAPlayer pkaPlayer) {
		pkaPlayers.put(playerName, pkaPlayer);
	}
	private static void removePKAPlayer(String playerName) {
		if (pkaPlayers.containsKey(playerName))
			pkaPlayers.remove(playerName);
	}

	/**
	 * used for when player joins (could be first time)
	 * @param player
	 */
	public static void loadPlayer(Player player) {
		String playerName = player.getName();
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);
		
		ClassType classType = getClassTypeFromPlayerConfig(playerName, playerConfig);
		if (classType == null) {
			MessageUtil.severe("Had to load a players config a second time!");
			playerConfig = FileUtil.getPlayerConfig(playerName);
			classType = getClassTypeFromPlayerConfig(playerName, playerConfig);
			if (classType == null) {
				MessageUtil.severe("Failed to load a players config a second time, disabling plugin");
				plugin.disable();
				return;
			}
		} else {
			loadPlayer(player, classType);
		}
	}
	
	/**
	 * do not use this if the player is joining
	 * @param player
	 * @param classTypeString
	 */
	private static void loadPlayer(Player player, ClassType classType) {		
		String playerName = player.getName();
		if (classType == ClassType.NONE) {
			MessageUtil.sendMessage(player, "Select a class before you can start playing.", MessageType.SINGLE);
			return;
		}
		
		PKAPlayer pkaPlayer = getInitialPKAPlayer(player, classType);
		addInitialArmorAttributesToPKAPlayer(player, pkaPlayer);
		addPKAPlayer(playerName, pkaPlayer);
		
		updateHealth(Bukkit.getPlayer(playerName), pkaPlayer);

		if (!hasStatItem(player)) {
			ItemUtil.updateStatItemMeta(player, pkaPlayer);
		}
		
		MessageUtil.log("player " + playerName + " has been loaded in.");
	}

	public static void loadAllPlayers() {
		for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {
			loadPlayer(Bukkit.getOnlinePlayers()[i]);
		}
	}
	
	public static PKAPlayer getInitialPKAPlayer(Player player, ClassType classType) {
		String playerName = player.getName();
		String classTypeString = classType.toString();
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);
		
		int level = 					getLevelFromPlayerConfig(playerName, playerConfig, classTypeString);
		int maxHealth = 				getMaxHealthFromPlayerConfig(playerName, playerConfig, classTypeString);
		int health = 					getHealthFromPlayerConfig(playerName, playerConfig, classTypeString);
		int[] attributes = 				getAttributesFromPlayerConfig(playerName, playerConfig, classTypeString);
		double damage = 				getDamageFromPlayerConfig(playerName, classTypeString, level);
		Inventory abilityInventory = 	Bukkit.createInventory(null, 9, "Abilities");
		int weaponSlot = 				InventoryUtil.getWeaponSlot(player);
		int availableUpgradePoints = 	getUpgradePointsFromPlayerConfig(playerName, playerConfig, classTypeString);
		
		int goldValue = 				getGoldAmountFromPlayerConfig(playerName, playerConfig, classTypeString);
		// Mining
		int miningExp = 				getMiningExpFromPlayerConfig(playerName, playerConfig, classTypeString);
		int miningLevel = 				getMiningLevelFromPlayerConfig(playerName, playerConfig, classTypeString);
		
		player.setLevel(level);
		
		return new PKAPlayer(playerName, classType, maxHealth, 
				health, attributes, damage, abilityInventory, weaponSlot, availableUpgradePoints, miningExp, miningLevel, goldValue);	
	}

	private static boolean hasLoadedClassBefore(String playerName, ClassType classType) {
		return FileUtil.getPlayerConfig(playerName).contains(classType.toString());
	}

	private static void writeNewClassToPlayerConfig(String playerName, String classTypeString) {
		writeNewClassToPlayerConfig(playerName, FileUtil.getPlayerConfig(playerName), classTypeString);
	}

	private static void writeNewClassToPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		// CHANGED TO INT, MIGHT BREAK IT. Marcus
		List<Integer> attributes = new ArrayList<Integer>();
		attributes.add(0); attributes.add(0); attributes.add(0); attributes.add(0);
		
		playerConfig.set(classTypeString + ".maxhealth", 100.0);
		playerConfig.set(classTypeString + ".health", 100.0);
		playerConfig.set(classTypeString + ".attributes", attributes);
		playerConfig.set(classTypeString + ".availableupgradepoints", 0);
		playerConfig.set(classTypeString + ".level", 1);
		
		playerConfig.set(classTypeString + ".gold", 0);
		// Mining
		playerConfig.set(classTypeString + ".mining.exp", 0);
		playerConfig.set(classTypeString + ".mining.level", 1);

		FileUtil.save(playerConfig, "plugins/PKAAdventure/players/" + playerName + ".yml");
	}

	private static void writeDefaultPlayerConfig(String playerName) {
		FileUtil.writeDefaultFile(new File("plugins/PKAAdventure/players/" + playerName + ".yml"), "defaultplayer.yml");
	}

	private static ClassType getClassTypeFromPlayerConfig(String playerName, YamlConfiguration playerConfig) {
		if (!playerConfig.contains("current_class_type")) {
			writeDefaultPlayerConfig(playerName);
			return ClassType.NONE;
		} else {
			return ClassType.valueOf(playerConfig.getString("current_class_type"));
		}
	}

	private static int getLevelFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		int level = 1;
		if (!playerConfig.contains(classTypeString + ".level")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
		} else {
			level = playerConfig.getInt(classTypeString + ".level");
		}
		return level;
	}

	private static int getHealthFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		if (!playerConfig.contains(classTypeString + ".maxhealth")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 100;
		} else {
			return playerConfig.getInt(classTypeString + ".maxhealth");
		}
	}
	
	private static int getUpgradePointsFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		if (!playerConfig.contains(classTypeString + ".availableupgradepoints")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 0;
		} else {
			return playerConfig.getInt(classTypeString + ".availableupgradepoints");
		}
	}

	private static int[] getAttributesFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		if (!playerConfig.contains(classTypeString + ".attributes")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return new int[]{0, 0, 0, 0};
		} else {
			List<Integer> stringList = playerConfig.getIntegerList(classTypeString + ".attributes");
			int[] stringArray = new int[4];
			for (int i = 0; i < 4; i++) {
				stringArray[i] = stringList.get(i);
			}
			return stringArray;
		}
	}

	private static double getDamageFromPlayerConfig(String playerName, String classTypeString, int level) {
		return MathUtil.getValue(level, classTypeString.toLowerCase() + "_damage");
	}
	
	private static int getMaxHealthFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		if (!playerConfig.contains(classTypeString + ".maxhealth")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 100;
		} else {
			return playerConfig.getInt(classTypeString + ".maxhealth");
		}
	}
	
	// Marcus mining
	private static int getMiningExpFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		if (!playerConfig.contains(classTypeString + ".mining.exp")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 0;
		} else {
			return playerConfig.getInt(classTypeString + ".mining.exp");
		}
	}
	
	private static int getMiningLevelFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		if (!playerConfig.contains(classTypeString + ".mining.level")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 1;
		} else {
			return playerConfig.getInt(classTypeString + ".mining.level");
		}
	}
	
	private static int getGoldAmountFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		if (!playerConfig.contains(classTypeString + ".gold")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 0;
		} else {
			return playerConfig.getInt(classTypeString + ".gold");
		}
	}

	/**
	 * using when player leaves game
	 * @param player Player specified to unload.
	 */
	public static void unloadPlayer(Player player) {
		savePlayer(player);
		removePKAPlayer(player.getName());
		//TODO
	}

	/**
	 * used when reloading
	 * @param player Player specified to reload.
	 */
	public static void reloadPlayer(Player player) {
		savePlayer(player);
	}

	public static void switchClass(Player player, ClassType classType) {
		if (classType == ClassType.NONE)
			return;

		String playerName = player.getName();
		String classTypeString = classType.toString();

		//TODO REMOVE BELOW (will load inventory in the future)
		ItemStack weapon = ItemUtil.getInitialItem(classTypeString.toLowerCase() + "_weapon", player.getLevel(), 1);
		ItemUtil.updateWeaponLore(weapon, classType, player.getLevel());
		InventoryUtil.setItem(player, InventoryUtil.getActualWeaponSlot(player), weapon);

		savePlayer(player);

		if(hasLoadedClassBefore(playerName, classType)) {
			loadPlayer(player, classType);
		} else {
			writeNewClassToPlayerConfig(playerName, classTypeString);
		}

		PlayerProcessor.addPKAPlayer(playerName, getInitialPKAPlayer(player, classType));
		//TODO Inventory
	}

	private static void savePlayer(Player player) {
		String playerName = player.getName();
		PKAPlayer pkaPlayer = getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);

		String classTypeString = pkaPlayer.getClassType().toString();
		playerConfig.set("current_class_type", classTypeString);
		playerConfig.set(classTypeString + ".maxhealth", pkaPlayer.getMaxHealth());
		playerConfig.set(classTypeString + ".health", pkaPlayer.getHealth());

		List<Integer> attributes = new ArrayList<Integer>();
		int[] intList = pkaPlayer.getAttributes();
		for (int i = 0; i < intList.length; i++) {
			if(intList[i] != 0)
				attributes.add(i, intList[i]);
			else
				attributes.add(i, 0);
		}
		
		playerConfig.set(classTypeString + ".attributes", attributes);
		playerConfig.set(classTypeString + ".availableupgradepoints", pkaPlayer.getAvailableUpgradePoints());
		playerConfig.set(classTypeString + ".level", player.getLevel());
		
		playerConfig.set(classTypeString + ".gold", pkaPlayer.getGoldAmount());
		// Mining
		playerConfig.set(classTypeString + ".mining.exp", pkaPlayer.getMiningExp());
		playerConfig.set(classTypeString + ".mining.level", pkaPlayer.getMiningLevel());

		FileUtil.save(playerConfig, "plugins/PKAAdventure/players/" + playerName + ".yml");
	}

	private static boolean hasStatItem(Player player) {
		return ItemUtil.isStatItem(player.getInventory().getItem(17));
	}

	private static ItemStack giveStatPearl(Player player) {
		ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
		player.getInventory().setItem(17, itemStack);
		return itemStack;
	}

	private static ItemStack giveStatEye(Player player) {
		ItemStack itemStack = new ItemStack(Material.EYE_OF_ENDER);
		player.getInventory().setItem(17, itemStack);
		return itemStack;
	}

	private static void addInitialArmorAttributesToPKAPlayer(Player player, PKAPlayer pkaPlayer) {		
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = playerInventory.getArmorContents()[i];
			if (ItemUtil.isAttributeItem(itemStack))
				pkaPlayer.addAttributes(ItemUtil.getAttributesFromItemStack(itemStack));
		}
	}

	public static int getAvailableUpgradePoints(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		if (!playerConfig.contains(classTypeString + ".availableupgradepoints")) {
			writeNewClassToPlayerConfig(playerName, playerConfig, classTypeString);
			return 0;
		} else {
			int availableUpgradePoints = FileUtil.getIntValueFromConfig(FileUtil.getConfig(), classTypeString + ".availableupgradepoints", "config.yml");
			return availableUpgradePoints;
		}
	}

	/**
	 * will set your health depending on the custom health
	 * @param player
	 */
	public static void updateHealth(Player player) {
		updateHealth(player, pkaPlayers.get(player.getName()));
	}

	public static int[] getPlayerAttributes(String playerName) {
		return getPKAPlayer(playerName).getAttributes();
	}

	public double getPlayerDamage(String playerName) {
		return getPKAPlayer(playerName).getDamage();
	}

	private static void setHomeToNearestBeacon(Player player) {
		//TODO
	}

	private static void damageArmor(Player player) {
		//TODO
	}

	/*
	 * use updateHealth(player) please
	 */
	private static void updateHealth(Player player, PKAPlayer pkaPlayer) {
		double newHealth = 1d;
		newHealth = (pkaPlayer.getHealth() / pkaPlayer.getMaxHealth()) * 20d;
		if (newHealth > 20d || newHealth <= 0d) {
			player.setGameMode(GameMode.CREATIVE);
		}
		player.setHealth(newHealth);
	}

	public static void rewardExperience(String playerName, int experience) {
		Player player = null;
		try {player = Bukkit.getPlayer(playerName);} catch(NullPointerException ex) {return;}
		rewardExperience(player, experience);
	}

	private static void rewardExperience(Player player, int experience) {
		PKAPlayer pkaPlayer = 		getPKAPlayer(player);

		int experienceRequired = 	MathUtil.getValue(player.getLevel(), "experience_required");
		int experienceCurrent = 	pkaPlayer.getExperience();
		int experienceNew = 		experienceCurrent + experience;
		if (experienceNew > experienceRequired) {
			int experienceLeftover = experienceRequired - experienceCurrent;
			levelUp(player, pkaPlayer, experienceLeftover);
			pkaPlayer.setExperience(experienceLeftover);
			updateExperienceBar(player, experienceLeftover, MathUtil.getValue(player.getLevel(), "experience_required"));
		} else {
			pkaPlayer.setExperience(experienceNew);
			updateExperienceBar(player, experienceNew, experienceRequired);
		}
	}

	private static void levelUp(Player player, PKAPlayer pkaPlayer, int experienceLeftover) {
		int newLevel = player.getLevel() + 1;
		player.setLevel(newLevel);
		ItemStack weapon = null;
		String classTypeStringLowercase = pkaPlayer.getClassType().toString().toLowerCase();

		if (pkaPlayer.getWeaponSlot() != 9) {
			weapon = ItemUtil.getWeapon(player, pkaPlayer.getWeaponSlot());
		} else {
			weapon = ItemUtil.getWeapon(player);
		}

		if (weapon == null)
			return;

		pkaPlayer.addAvailableUpgradePoint();
		int newDamage = MathUtil.getValue(newLevel, classTypeStringLowercase + "_damage");
		pkaPlayer.setDamage(newDamage);

		int level = 			player.getLevel();
		ItemUtil.replaceValueInItemLore(weapon.getItemMeta().getLore(), pkaPlayer.getClassType().toString().toLowerCase() + "_damage", newDamage);
		
		ItemUtil.updateStatItemMeta(player, pkaPlayer);
	}

	private static void updateExperienceBar(Player player, int experienceNew, int experienceRequired) {
		float experienceBar = (float) experienceRequired / (float) experienceNew;
		if (experienceBar > 1f || experienceBar < 0f)
			experienceBar = 0.5f;
		player.setExp(experienceBar);
	}

	public static void damagePlayerByEnvironment(Player player, double minecraftDamage) {
		if (minecraftDamage < 5d)
			return;
		else {
			PKAPlayer pkaPlayer = getPKAPlayer(player);
			if (pkaPlayer == null)
				return;
			double maxHealth = pkaPlayer.getMaxHealth();
			double finalDamage = DamageUtil.getFinalizedDamage(minecraftDamage, maxHealth);
			damagePlayer(player, pkaPlayer, finalDamage);
		}
	}

	public static void damagePlayerByEntity(Player player, PKAPlayer pkaPlayer, double damage, int[] attributesAttacker) {
		double finalDamage = DamageUtil.getFinalizedDamage(damage, attributesAttacker, pkaPlayer.getAttributes());
		damagePlayer(player, pkaPlayer, finalDamage);
	}

	private static void damagePlayer(Player player, PKAPlayer pkaPlayer, double damage) {
		if (damage <= 0d)
			return;
		double finalHealth = pkaPlayer.getHealth() - damage;
		if (finalHealth > 0)
			damagePlayerNonLethal(player, pkaPlayer, finalHealth);
		else {
			damagePlayerLethal(player);
		}
	}

	private static void damagePlayerNonLethal(Player player, PKAPlayer pkaPlayer, double newHealth) {
		pkaPlayer.setHealth(newHealth);
		updateHealth(player, pkaPlayer);
	}

	private static void damagePlayerLethal(Player player) {
		player.setHealth(0);
	}

	public static void playerDeath(Entity entity) {
		Player player = (Player) entity;
		PKAPlayer pkaPlayer = getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		pkaPlayer.setHealth(pkaPlayer.getMaxHealth());
		setHomeToNearestBeacon(player);
		damageArmor(player);
		MessageUtil.sendMessage(player, "You died, your armor has been damaged.", MessageType.SINGLE);
	}

}
