package com.pkadev.pkaadventure.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.LocationUtil;
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
	public static void removePKAPlayer(Player player) {
		removePKAPlayer(player.getName());
	}
	private static void removePKAPlayer(String playerName) {
		if (pkaPlayers.containsKey(playerName))
			pkaPlayers.remove(playerName);
	}

	@SuppressWarnings("deprecation")
	public static void loadAllPlayers() {
		for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {
			loadPlayer(Bukkit.getOnlinePlayers()[i]);
		}
	}
	
	/**
	 * using when player leaves game
	 * @param player Player specified to unload.
	 */
	public static void unloadPlayer(Player player) {
		savePlayer(player);
		removePKAPlayer(player.getName());
	}

	/**
	 * used when reloading
	 * @param player Player specified to reload.
	 */
	public static void reloadPlayer(Player player) {
		savePlayer(player);
	}
	
	/**
	 * do not use this if the player is joining
	 * @param player
	 * @param classTypeString
	 */
	public static void loadPlayer(Player player) {
		String playerName = player.getName();
		loadPlayer(player, getClassTypeFromPlayerConfig(player.getName(), FileUtil.getPlayerConfig(playerName)));
	}
	
	private static void loadPlayer(final Player player, ClassType classType) {
		final String playerName = player.getName();
		if (classType == ClassType.NONE) {
			MessageUtil.sendMessage(player, "Select a class before you can start playing.", MessageType.SINGLE);
			return;
		}
		
		PKAPlayer pkaPlayer = getInitialPKAPlayer(player, classType);
		setAttributes(player, pkaPlayer);
		addPKAPlayer(playerName, pkaPlayer);
		
		player.setLevel(pkaPlayer.getLevel());
		updateHealth(Bukkit.getPlayer(playerName), pkaPlayer);
		updateExperience(player, pkaPlayer);
		ItemUtil.giveWeapon(player, classType);
		InventoryUtil.loadInventory(player, "Ability", playerName);
		
		MessageUtil.log("player " + playerName + " has been loaded in.");
	}
	
	private static PKAPlayer getInitialPKAPlayer(Player player, ClassType classType) {
		String playerName = player.getName();
		String classTypeString = classType.toString();
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);
		
		int level = 					getLevelFromPlayerConfig(playerName, playerConfig, classTypeString);
		int experience = 				getExperienceFromPlayerConfig(playerName, playerConfig, classTypeString);
		int maxHealth = 				getMaxHealthFromPlayerConfig(playerName, playerConfig, classTypeString);
		int health = 					getHealthFromPlayerConfig(playerName, playerConfig, classTypeString);
		double damage = 				getDamageFromPlayerConfig(playerName, classTypeString, level);
		int weaponSlot = 				InventoryUtil.getWeaponSlot(player);
		int availableUpgradePoints = 	getUpgradePointsFromPlayerConfig(playerName, playerConfig, classTypeString);
		List<String> discovLocations = 	getDiscoveredLocations(playerName, playerConfig, classTypeString);
		
		int goldValue = 				getGoldAmountFromPlayerConfig(playerName, playerConfig, classTypeString);
		// Mining
		int miningExp = 				getMiningExpFromPlayerConfig(playerName, playerConfig, classTypeString);
		int miningLevel = 				getMiningLevelFromPlayerConfig(playerName, playerConfig, classTypeString);
		
		return new PKAPlayer(player, classType, level, experience, maxHealth, 
				health, damage, weaponSlot, availableUpgradePoints, miningExp, miningLevel, goldValue, discovLocations);	
	}

	private static boolean hasLoadedClassBefore(String playerName, ClassType classType) {
		return FileUtil.getPlayerConfig(playerName).contains(classType.toString());
	}

	private static void writeNewClassToPlayerConfig(String playerName, String classTypeString) {
		writeNewClassToPlayerConfig(playerName, FileUtil.getPlayerConfig(playerName), classTypeString);
	}

	private static void writeNewClassToPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		List<String> emptyList = new ArrayList<String>();
		emptyList.add("default");
		
		playerConfig.set(classTypeString + ".level", 1);
		playerConfig.set(classTypeString + ".experience", 0);
		playerConfig.set(classTypeString + ".maxhealth", 100.0);
		playerConfig.set(classTypeString + ".health", 100.0);
		playerConfig.set(classTypeString + ".availableupgradepoints", 0);
		playerConfig.set(classTypeString + ".discoveredlocations", emptyList);
		
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
	
	private static int getExperienceFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		return playerConfig.getInt(classTypeString + ".experience");
	}
	
	private static int getUpgradePointsFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		return playerConfig.getInt(classTypeString + ".availableupgradepoints");
	}

	private static List<String> getDiscoveredLocations(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		return playerConfig.getStringList(classTypeString + ".discoveredlocations");
	}
	
	private static double getDamageFromPlayerConfig(String playerName, String classTypeString, int level) {
		return MathUtil.getValue(level, classTypeString.toLowerCase() + "_damage");
	}
	
	private static int getMaxHealthFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		return playerConfig.getInt(classTypeString + ".maxhealth");
	}
	
	private static int getHealthFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {
		return playerConfig.getInt(classTypeString + ".health");
	}
	
	// Marcus mining
	private static int getMiningExpFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		return playerConfig.getInt(classTypeString + ".mining.exp");
	}
	
	private static int getMiningLevelFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		return playerConfig.getInt(classTypeString + ".mining.level");
	}
	
	private static int getGoldAmountFromPlayerConfig(String playerName, YamlConfiguration playerConfig, String classTypeString) {	
		return playerConfig.getInt(classTypeString + ".gold");
	}

	public static void switchClass(final Player player, final ClassType classType) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				savePlayer(player);
				InventoryUtil.clearInventory(player);
				
				if (classType == ClassType.NONE)
					return;

				String playerName = player.getName();
				String classTypeString = classType.toString();
				PlayerProcessor.addPKAPlayer(playerName, getInitialPKAPlayer(player, classType));
				
				ItemUtil.giveWeapon(player, classType);

				if(!hasLoadedClassBefore(playerName, classType)) {
					writeNewClassToPlayerConfig(playerName, classTypeString);
				}
				
				savePlayerClass(playerName, classTypeString);
				loadPlayer(player, classType);
				InventoryUtil.loadInventory(player, "PlayerInventory", playerName);
				InventoryUtil.loadArmorContent(player, playerName);
			}
			
		});
	}
	
	public static boolean isPlayer(LivingEntity livingEntity) {
		return livingEntity instanceof Player;
	}
	
	public static Player getPlayer(LivingEntity livingEntity) {
		return (Player) livingEntity;
	}

	private static void savePlayer(Player player) {
		String playerName = player.getName();
		PKAPlayer pkaPlayer = getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);

		String classTypeString = pkaPlayer.getClassType().toString();
		playerConfig.set(classTypeString + ".level", player.getLevel());
		playerConfig.set(classTypeString + ".experience", pkaPlayer.getExperience());
		playerConfig.set(classTypeString + ".maxhealth", pkaPlayer.getMaxHealth());
		playerConfig.set(classTypeString + ".health", pkaPlayer.getHealth());
		playerConfig.set(classTypeString + ".availableupgradepoints", pkaPlayer.getAvailableUpgradePoints());
		
		playerConfig.set(classTypeString + ".gold", pkaPlayer.getGoldAmount());
		// Mining
		playerConfig.set(classTypeString + ".mining.exp", pkaPlayer.getMiningExp());
		playerConfig.set(classTypeString + ".mining.level", pkaPlayer.getMiningLevel());

		FileUtil.save(playerConfig, "plugins/PKAAdventure/players/" + playerName + ".yml");
		
		InventoryUtil.saveInventory(player.getInventory(), "PlayerInventory", playerName);
		InventoryUtil.saveInventory(pkaPlayer.getAbilityInventory(), "Ability", playerName);
	}
	
	private static void savePlayerClass(String playerName, String classTypeString) {
		YamlConfiguration playerConfig = FileUtil.getPlayerConfig(playerName);
		playerConfig.set("current_class_type", classTypeString);
		
		FileUtil.save(playerConfig, "plugins/PKAAdventure/players/" + playerName + ".yml");
	}

	public static void setAttributes(Player player, PKAPlayer pkaPlayer) {
		pkaPlayer.clearAttributes();
		pkaPlayer.addAttributes(InventoryUtil.getAttributesFromArmorContent(player));
		ItemUtil.updateStatItemMeta(player, pkaPlayer);
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
	
	public static int[] getPlayerAttributes(String playerName) {
		return getPKAPlayer(playerName).getAttributes();
	}

	public double getPlayerDamage(String playerName) {
		return getPKAPlayer(playerName).getDamage();
	}
	
	private static void applyDeathEffect(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 2));
	}

	public static void rewardExperience(String playerName, int experience) {
		Player player = null;
		try {player = Bukkit.getPlayer(playerName);} catch(NullPointerException ex) {return;}
		rewardExperience(player, experience);
	}

	private static void rewardExperience(Player player, int experience) {
		PKAPlayer pkaPlayer = 		getPKAPlayer(player);
		pkaPlayer.addExperience(experience);
		updateExperience(player, pkaPlayer);
	}

	private static void levelUp(Player player, PKAPlayer pkaPlayer) {
		ItemStack weapon = null;
		String classTypeStringLowercase = pkaPlayer.getClassType().toString().toLowerCase();

		if (pkaPlayer.getWeaponSlot() != 9) {
			weapon = InventoryUtil.getWeapon(player, pkaPlayer.getWeaponSlot());
		} else {
			weapon = InventoryUtil.getWeapon(player);
		}

		if (weapon == null)
			return;

		pkaPlayer.addAvailableUpgradePoint();
		int newDamage = MathUtil.getValue(pkaPlayer.getLevel(), classTypeStringLowercase + "_damage");
		pkaPlayer.setDamage(newDamage);

		ItemUtil.replaceValueInItemLore(weapon.getItemMeta().getLore(), pkaPlayer.getClassType().toString().toLowerCase() + "_damage", newDamage);
		ItemUtil.updateStatItemMeta(player, pkaPlayer);
	}

	private static void updateExperience(Player player, PKAPlayer pkaPlayer) {
		float experience = pkaPlayer.getExperience();
		float experienceRequired = pkaPlayer.getExperienceRequired();
		
		if (experience > experienceRequired) {
			experience = experienceRequired - experience;
			pkaPlayer.addLevel();
			pkaPlayer.setExperienceRequired(MathUtil.getValue(pkaPlayer.getLevel(), "experience_required"));
			player.setLevel(pkaPlayer.getLevel());
			levelUp(player, pkaPlayer);
			updateExperience(player, pkaPlayer);
		} else {
			float experienceBar = (float) pkaPlayer.getExperienceRequired() / (float) pkaPlayer.getExperience();
			if (experienceBar > 1f || experienceBar < 0f)
				experienceBar = 0.5f;
			player.setExp(experienceBar);
		}
	}

	public static void damagePlayerLethal(Player player) {
		PKAPlayer pkaPlayer = getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		pkaPlayer.setHealth(pkaPlayer.getMaxHealth());
		LocationUtil.teleportToBeacon(player, true);
		applyDeathEffect(player);
		InventoryUtil.damageArmorContent(player, pkaPlayer);
		MessageUtil.sendMessage(player, "You died, your armor has been damaged.", MessageType.SINGLE);
	}

}
