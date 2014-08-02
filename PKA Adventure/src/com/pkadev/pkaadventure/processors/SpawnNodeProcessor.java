package com.pkadev.pkaadventure.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieEvil;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieGood;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieNeutral;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombiePassive;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.types.MobType;
import com.pkadev.pkaadventure.types.SpawnNodeType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MathUtil;

public class SpawnNodeProcessor {
	private static Main plugin = Main.instance;
	private static Random random = new Random();
	private static WorldServer world = ((CraftWorld) Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"))).getHandle();
	
	//1 = mobs, 2 = beacons, 3 = lootcrate
	private static HashMap<SpawnNode, Integer> 	list1 = new HashMap<SpawnNode, Integer>();
	private static List<SpawnNode> 				list2 = new ArrayList<SpawnNode>();
	private static HashMap<SpawnNode, Integer> 	list3 = new HashMap<SpawnNode, Integer>();
	
	private static void addNodeToList1(SpawnNode node) {
		list1.put(node, -1);
	}
	private static void addNodeToList2(SpawnNode node) {
		list2.add(node);
	}
	private static void addNodeToList3(SpawnNode node) {
		list3.put(node, 5);
	}
	
	public static List<SpawnNode> getBeaconList() {
		return list2;
	}
	
	public static void addSpawnNode(SpawnNode node, int listNumber) {
		if (listNumber == 1) {
			addNodeToList1(node);
		} else if (listNumber == 2) {
			addNodeToList2(node);
		} else if (listNumber == 3) {
			addNodeToList3(node);
		}
	}
	
	public static void load() {
		YamlConfiguration spawnNodeConfig = FileUtil.getSpawnNodeConfig();
		
		if (spawnNodeConfig.contains("Mobs")) {
			int amount = 0;
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Mobs");
			load(section, 1);
			plugin.log("loaded " + amount + " mob SpawnNodes.");
		}
		
		if (spawnNodeConfig.contains("Beacons")) {
			int amount = 0;
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Beacons");
			load(section, 2);
			plugin.log("loaded " + amount + " beacons.");
			
			if (amount == 0) {
				addDefaultBeaconToList2();
			}
		} else {
			addDefaultBeaconToList2();
		}
		
		if (spawnNodeConfig.contains("Lootcrates")) {
			int amount = 0;
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Lootcrates");
			load(section, 3);
			plugin.log("loaded " + amount + " lootcrates.");
		}
		
		startTimer();
	}
	
	private static int load(ConfigurationSection section, int listNumber) {
		//create fileName mobName radius level amount mob strength stance type
		int amount = 0;
		for (String nodeName : section.getKeys(false)) {
			SpawnNode node = null;
			if (listNumber == 1) {
				try {
					node = new SpawnNode(getLocation(section), section.getString(".Name"), section.getInt(".Radius"), section.getInt(".Level")
							, section.getInt(".Amount"), section.getString(".Mob"), MobStrength.valueOf(section.getString(".MobStrength"))
							, MobStance.valueOf(section.getString(".MobStance")), MobType.valueOf(section.getString(".MobType")));
				} catch (Exception ex) {
					plugin.severe("critical loading error for spawnNode by fileName " + nodeName + ". It cannot be loaded!");
					continue;
				}
			} else {
				node = new SpawnNode(getLocation(section));
			}
			
			addSpawnNode(node, listNumber);
			amount ++;
		}
		return amount;
	}
	
	private static Location getLocation(ConfigurationSection section) {
		String prefix = ".Location.";
		World world = Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"));
		int x = section.getInt(prefix + "X");
		int y = section.getInt(prefix + "Y");
		int z = section.getInt(prefix + "Z");
		return new Location(world, x, y, z);
	}
	
	private static void startTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				tickList1();
				tickList3();
			}
			
		}, 500, 100);
	}
	
	//tick for list1 (mobs)
	private static void tickList1() {
		Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
		World defaultWorld = Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"));
		List<Location> onlinePlayerLocations = new ArrayList<Location>();
		
		for (Player player : onlinePlayers) {
			if (player.getWorld() == defaultWorld) 
				onlinePlayerLocations.add(player.getLocation());
		}
		
		for (SpawnNode node : list1.keySet()) {
			int value = list1.get(node);
			boolean isAnyPlayerClose = isAnyPlayerClose(node.getLocation(), onlinePlayerLocations);
			if (value == -1) {
				//inactive (check if player is close)
				if (isAnyPlayerClose) {
					spawn(node);
					list1.put(node, 0);
				}
			} else {
				//active (check if player is close, then check other things)
				if (isAnyPlayerClose) {
					if (value == 0) {
						//active (check if a mob is dead)
						if (isAnyMobDead(node)) {
							list1.put(node, node.getTicksToRefresh());
						}
					} else if (value == 1) {
						//active (refresh the node, some of the mobs are dead)
						refresh(node);
						list1.put(node, 0);
					} else {
						//active (missing mobs but not ready to respawn yet)
						list1.put(node, value - 1);
					}
				} else {
					despawn(node);
					list1.put(node, -1);
				}
			}
		}
	}
	
	//tick for list3 (lootcrates)
	private static void tickList3() {
		List<SpawnNode> toBeRemoved = new ArrayList<SpawnNode>();
		for (SpawnNode node : list3.keySet()) {
			int value = list3.get(node);
			if (value == 1) {
				//refresh the node
				toBeRemoved.add(node);
			} else {
				list3.put(node, value - 1);
			}
		}
		for (SpawnNode node : toBeRemoved) {
			list3.remove(node);
		}
	}
	
	private static void addDefaultBeaconToList2() {
		addNodeToList2(new SpawnNode(
				new Location(
						Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml")), 
						1, 64, 1)));
	}
	
	private static boolean isAnyPlayerClose(Location location, List<Location> onlinePlayerLocations) {
		for (Location playerLocation : onlinePlayerLocations) {
			if (isPlayerClose(location, playerLocation))
				return true;
		}
		return false;
	}
	
	private static boolean isPlayerClose(Location location, Location playerLocation) {
		return location.distanceSquared(playerLocation) <= 144;
	}
	
	/**
	 * ONLY USE FOR MOB NODES!
	 * @param node
	 * @return
	 */
	private static boolean isAnyMobDead(SpawnNode node) {
		return node.getLiveMobAmount() < node.getAmount();
	}
	
	public static void addMobToNode(SpawnNode node, MobMonster mobMonster) {
		node.addLiveMob(mobMonster);
	}
	
	public static void removeMobFromNode(SpawnNode node, MobMonster mobMonster) {
		node.getLiveMobs().remove(mobMonster);
	}
	
	public static Location getNearestBeacon(Location playerLocation) {
		double shortestDistanceSquared = 0d;
		Location shortestDistanceLocation = null;
		
		for (SpawnNode node : getBeaconList()) {
			Location beaconLocation = node.getLocation();
			double distanceSquared = beaconLocation.distanceSquared(playerLocation);
			
			if (distanceSquared < shortestDistanceSquared) {
				shortestDistanceSquared = distanceSquared;
				shortestDistanceLocation = beaconLocation;
			} else if (shortestDistanceSquared == 0d) {
				shortestDistanceSquared = distanceSquared;
				shortestDistanceLocation = beaconLocation;
			}
		}
		
		return shortestDistanceLocation;
	}
	
	public static void spawn(SpawnNode node) {
		SpawnNodeType spawnNodeType = node.getSpawnNodeType();
		switch (spawnNodeType) {
		case LOOTCRATE: {
			//TODO
		}
		case MOB: {
			spawnMobs(node, node.getAmount());
		}
		default:return;
		}
	}
	
	private static void spawnMobs(SpawnNode node, int amount) {
		for (int i = 0; i < node.getAmount(); i++) {
			MobMonster mobMonster = getMobMonster(node);
			initiateMobMonster(mobMonster, node);
			addMobToNode(node, mobMonster);
			setSpawnLocation(node.getLocation(), mobMonster, node.getRadius());
			world.addEntity(mobMonster.getEntity());
		}
	}

	public static void refresh(SpawnNode node) {
		int amount = node.getAmount() - node.getLiveMobAmount();
		spawnMobs(node, amount);
	}

	public static void despawn(SpawnNode node) {
		for (int i = 0; i < node.getLiveMobAmount(); i++) {
			MobMonster mobMonster = node.getLiveMobs().get(i);
			world.removeEntity(mobMonster.getEntity());
		}
		node.getLiveMobs().clear();
	}
	
	public static void openLootcrate(Location location, Player player) {
		//TODO
		addSpawnNode(new SpawnNode(location), 3);
	}
	
	public static boolean newSpawnNode(Location location, String[] args, String fileName) {
		SpawnNode node = null;
		
		if (args == null) {
			//beacon
			node = new SpawnNode(location);
			addNodeToList2(node);
			saveSpawnNode(node, fileName);
			return true;
		} else if (args.length == 1) {
			//lootcrate
			node = new SpawnNode(location, 0);
			addNodeToList3(node);
			saveSpawnNode(node, fileName);
			return true;
		} else if (args.length == 8) {
			try {
				node = new SpawnNode(location, args[0].replace('_', ' '), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], MobStrength.valueOf(args[5].toUpperCase()), MobStance.valueOf(args[6].toUpperCase()), MobType.valueOf(args[7].toUpperCase()));
			} catch (Exception ex) {
				return false;
			}
			addNodeToList2(node);
			saveSpawnNode(node, fileName);
			return true;
		} else {
			return false;
		}
	}
	
	private static void saveSpawnNode(SpawnNode node, String fileName) {
		YamlConfiguration spawnNodeConfig = FileUtil.getSpawnNodeConfig();
		String prefix = "Lootcrates." + fileName;
		if (node.getSpawnNodeType() == SpawnNodeType.BEACON) {
			prefix = "Beacons." + fileName;
		} else if (node.getSpawnNodeType() == SpawnNodeType.MOB) {
			prefix = "Mobs." + fileName;
			spawnNodeConfig.set(prefix + ".Name", 			node.getName());
			spawnNodeConfig.set(prefix + ".Radius", 		node.getRadius());
			spawnNodeConfig.set(prefix + ".Level", 			node.getLevel());
			spawnNodeConfig.set(prefix + ".Amount", 		node.getAmount());
			spawnNodeConfig.set(prefix + ".Mob", 			node.getMob());
			spawnNodeConfig.set(prefix + ".MobStrength", 	node.getMobStrength().toString());
			spawnNodeConfig.set(prefix + ".MobStance", 		node.getMobStance().toString());
			spawnNodeConfig.set(prefix + ".MobType", 		node.getMobType().toString());
		}
		Location location = node.getLocation();
		spawnNodeConfig.set(prefix + ".Location.X", 		(int) location.getX());
		spawnNodeConfig.set(prefix + ".Location.X", 		(int) location.getY());
		spawnNodeConfig.set(prefix + ".Location.X", 		(int) location.getZ());
	}
	
	/**
	 * gives random location in the radius around the location of the node
	 * @param nodeLocation
	 * @param nodeRadius
	 * @return
	 */
	private static void setSpawnLocation(Location nodeLocation, MobMonster mobMonster, int nodeRadius) {
		int x = (int) (nodeLocation.getX() - nodeRadius + (random.nextInt(nodeRadius) * 2));
		int y = (int) (nodeLocation.getY() - nodeRadius + (random.nextInt(nodeRadius) * 2));
		int z = (int) (nodeLocation.getZ() - nodeRadius + (random.nextInt(nodeRadius) * 2));
		float pitch = random.nextFloat();
		float yaw = random.nextFloat();
		mobMonster.getEntity().setLocation(x, y, z, pitch, yaw);
	}
	
	private static MobMonster getMobMonster(SpawnNode node) {
		String customEntityTypeString = "CustomEntity" + node.getMob() + node.getMobStance().toString();
		MobMonster mobMonster = getMobMonster(customEntityTypeString, node.getMobStance());
		return mobMonster;
	}
	
	private static MobMonster getMobMonster(String customEntityTypeString, MobStance mobStance) {
		switch(mobStance) {
			case EVIL:{
				switch(customEntityTypeString.toLowerCase()) {
					case "zombie":return new CustomEntityZombieEvil(world);
					default:return null;
				}
			}
			case GOOD:{
				switch(customEntityTypeString.toLowerCase()) {
				case "zombie":return new CustomEntityZombieGood(world);
				default:return null;
			}
			}
			case NEUTRAL:{
				switch(customEntityTypeString.toLowerCase()) {
				case "zombie":return new CustomEntityZombieNeutral(world);
				default:return null;
			}
			}
			case PASSIVE:{
				switch(customEntityTypeString.toLowerCase()) {
				case "zombie":return new CustomEntityZombiePassive(world);
				default:return null;
			}
			}
			default:return null;
		}
	}
	
	private static void initiateMobMonster(MobMonster mobMonster, SpawnNode node) {
		if (mobMonster == null)
			return;
		String mobName = 			node.getName();
		int[] attributes = 			getInitialAttributes(node);
		double maxHealth = 			getInitialMaxHealth(node);
		double damage = 			getInitialDamage(node);
		int level = 				node.getLevel();
		MobStrength mobStrength = 	node.getMobStrength();
		MobStance mobStance = 		node.getMobStance();
		MobType mobType = 			node.getMobType();
		int rareItemInt = 			setInitialArmorContent(mobMonster, node);
		
		
		PKAMob pkaMob = new PKAMob(mobName, attributes, maxHealth, damage, level,
				mobStrength, mobStance, mobType, rareItemInt);
		mobMonster.setPKAMob(pkaMob);
		mobMonster.setSpawnNode(node);
	}
	
	private static double getInitialMaxHealth(SpawnNode node) {
		double health = (double) MathUtil.getValue(node.getLevel(), "mob_maxhealth");
		return MobProcessor.addMobStrengthMultiplier(health, node.getMobStrength());
	}
	private static int[] getInitialAttributes(SpawnNode node) {
		int[] attributes = MathUtil.getArray(node.getLevel(), new String[]{"mob_strength", "mob_toughness", "mob_agility", "mob_restoration"});
		if (attributes == null || attributes.length != 4)
			return null;
		for (int i = 0; i < 4; i++) {
			attributes[i] = MobProcessor.addMobStrengthMultiplier(attributes[i], node.getMobStrength());
		}
		return attributes;
	}
	private static double getInitialDamage(SpawnNode node) {
		double damage = (double) MathUtil.getValue(node.getLevel(), "mob_damage");
		return MobProcessor.addMobStrengthMultiplier(damage, node.getMobStrength());
	}
	private static int setInitialArmorContent(MobMonster mobMonster, SpawnNode node) {
		int rareItemInt = -1;
		if (random.nextInt(4) > 2) {
			rareItemInt = random.nextInt(4);
		}
		((LivingEntity) mobMonster.getEntity().getBukkitEntity()).getEquipment().setArmorContents(ItemUtil.getInitialContent(node.getLevel(), rareItemInt));
		return rareItemInt;
	}
}
