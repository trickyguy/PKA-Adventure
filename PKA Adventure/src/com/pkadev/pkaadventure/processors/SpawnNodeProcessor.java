package com.pkadev.pkaadventure.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityVillagerNPC;
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
import com.pkadev.pkaadventure.utils.MessageUtil;

public class SpawnNodeProcessor {
	private static Main plugin = Main.instance;
	private static Random random = new Random();
	private static WorldServer worldServer = ((CraftWorld) Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"))).getHandle();
	private static World world = Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"));
	
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
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Mobs");
			int amount = load(section, 1);
			MessageUtil.log("loaded " + amount + " mob nodes.");
		}
		
		if (spawnNodeConfig.contains("Beacons")) {
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Beacons");
			int amount = load(section, 2);
			MessageUtil.log("loaded " + amount + " beacons.");
			if (amount == 0) {
				addDefaultBeaconToList2();
			}
		} else {
			addDefaultBeaconToList2();
		}
		
		if (spawnNodeConfig.contains("Lootcrates")) {
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Lootcrates");
			load(section, 3);
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
					node = new SpawnNode(getLocation(section, nodeName), section.getString(nodeName + ".Name"), section.getInt(nodeName + ".Radius"), section.getInt(nodeName + ".Level")
							, section.getInt(nodeName + ".Amount"), section.getString(nodeName + ".Mob"), MobStrength.valueOf(section.getString(nodeName + ".MobStrength").toUpperCase())
							, MobStance.valueOf(section.getString(nodeName + ".MobStance").toUpperCase()), MobType.valueOf(section.getString(nodeName + ".MobType").toUpperCase()));
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageUtil.severe("critical loading error for spawnNode by fileName " + nodeName + ". It cannot be loaded!");
					continue;
				}
			} else if (listNumber == 2) {
				node = new SpawnNode(getLocation(section, nodeName), section.getString(nodeName + ".Name"));
			} else {
				node = new SpawnNode(getLocation(section, nodeName));
			}
			addSpawnNode(node, listNumber);
			amount += 1;
		}
		return amount;
	}
	
	private static Location getLocation(ConfigurationSection section, String nodeName) {
		String prefix = nodeName + ".Location.";
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
		@SuppressWarnings("deprecation")
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
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
						1, 64, 1), "Default"));
	}
	
	private static boolean isAnyPlayerClose(Location location, List<Location> onlinePlayerLocations) {
		for (Location playerLocation : onlinePlayerLocations) {
			if (isPlayerClose(location, playerLocation))
				return true;
		}
		return false;
	}
	
	private static boolean isPlayerClose(Location location, Location playerLocation) {
		return location.distanceSquared(playerLocation) <= 256;
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
	
	public static SpawnNode getNearestBeacon(Location playerLocation) {
		double shortestDistanceSquared = 0d;
		SpawnNode nearestNode = null;
		
		for (SpawnNode node : getBeaconList()) {
			double distanceSquared = node.getLocation().distanceSquared(playerLocation);
			
			if (distanceSquared < shortestDistanceSquared) {
				shortestDistanceSquared = distanceSquared;
				nearestNode = node;
			} else if (shortestDistanceSquared == 0d) {
				shortestDistanceSquared = distanceSquared;
				nearestNode = node;
			}
		}
		
		return nearestNode;
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
			if (mobMonster == null) {
				return;
			}
			initiateMobMonster(mobMonster, node);
			addMobToNode(node, mobMonster);
			setSpawnLocation(node.getLocation(), mobMonster, node.getRadius());
			worldServer.addEntity(mobMonster.getEntity());
		}
	}

	public static void refresh(SpawnNode node) {
		int amount = node.getAmount() - node.getLiveMobAmount();
		spawnMobs(node, amount);
	}

	public static void despawn(SpawnNode node) {
		for (int i = 0; i < node.getLiveMobAmount(); i++) {
			MobMonster mobMonster = node.getLiveMobs().get(i);
			worldServer.removeEntity(mobMonster.getEntity());
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
			//lootcrate
			node = new SpawnNode(location);
			addNodeToList3(node);
			saveSpawnNode(node, fileName);
			return true;
		} else if (args.length == 1) {
			//beacons
			node = new SpawnNode(location, args[0]);
			addNodeToList2(node);
			saveSpawnNode(node, fileName);
			return true;
		} else if (args.length == 8) {
			
			String name = args[0].replace('_', ' ');
			int radius = 1;
			int level = 1;
			int amount = 0;
			String mob = args[4];
			MobStrength mobStrength = MobStrength.ANIMAL;
			MobStance mobStance = MobStance.PASSIVE;
			MobType mobType = MobType.PASSIVE;
			
			try {
				radius = Integer.parseInt(args[1]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Radius must be integer.");
				ex.printStackTrace();
				return false;
			}
			
			try {
				level = Integer.parseInt(args[2]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Level must be integer.");
				ex.printStackTrace();
				return false;
			}
			
			try {
				amount = Integer.parseInt(args[3]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Amount must be integer.");
				ex.printStackTrace();
				return false;
			}
			
			try {
				mobStrength = MobStrength.valueOf(args[5].toUpperCase());
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("MobStrength " + args[5] + " does not exist.");
				ex.printStackTrace();
				return false;
			}
			
			try {
				mobStance = MobStance.valueOf(args[6].toUpperCase());
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("MobStance " + args[6] + " does not exist.");
				ex.printStackTrace();
				return false;
			}
			
			try {
				mobType = MobType.valueOf(args[7].toUpperCase());
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("MobType " + args[7] + " does not exist.");
				ex.printStackTrace();
				return false;
			}
			
			
			node = new SpawnNode(location, name, radius, level, amount, mob, mobStrength, mobStance, mobType);
			
			addNodeToList1(node);
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
			spawnNodeConfig.set(prefix + ".Name",           node.getName());
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
		spawnNodeConfig.set(prefix + ".Location.Y", 		(int) location.getY());
		spawnNodeConfig.set(prefix + ".Location.Z", 		(int) location.getZ());
		FileUtil.save(spawnNodeConfig, "plugins/PKAAdventure/spawnnodes.yml");
	}
	
	/**
	 * gives random location in the radius around the location of the node
	 * @param nodeLocation
	 * @param nodeRadius
	 * @return
	 */
	private static void setSpawnLocation(Location nodeLocation, MobMonster mobMonster, int nodeRadius) {
		int x = (int) (nodeLocation.getX() - nodeRadius + (random.nextInt(nodeRadius) * 2) + 1);
		int y = (int) (nodeLocation.getY() - nodeRadius + (random.nextInt(nodeRadius) * 2) + 1);
		int z = (int) (nodeLocation.getZ() - nodeRadius + (random.nextInt(nodeRadius) * 2) + 1);
		float pitch = random.nextFloat();
		float yaw = random.nextFloat();
		mobMonster.getEntity().setLocation(x, y, z, pitch, yaw);
	}
	
	private static MobMonster getMobMonster(SpawnNode node) {
		MobMonster mobMonster = getInitialMobMonster(node);
		return mobMonster;
	}
	
	private static MobMonster getInitialMobMonster(SpawnNode node) {
		MobMonster mobMonster = null;
		switch(node.getMobStance()) {
		case EVIL:{
			switch(node.getMob()) {
			case "zombie":{
				mobMonster = new CustomEntityZombieEvil(worldServer);
				break;
			}
			default:return null;
			}
			break;
		}
		case GOOD: {
			switch(node.getMob()) {
			case "zombie": { 
				mobMonster = new CustomEntityZombieGood(worldServer);
				break;
			}
			default:return null;
			}
			break;
		}
		case NEUTRAL: {
			switch(node.getMob()) {
			case "zombie": {
				mobMonster = new CustomEntityZombieNeutral(worldServer);
				break;
			}
			default:return null;
			}
			break;
		}
		case PASSIVE: {
			switch(node.getMob()) {
			case "zombie": {
				mobMonster = new CustomEntityZombiePassive(worldServer);
				break;
			}
			default:return null;
			}
			break;
		}
		case NPC: {
			switch(node.getMob()) {
			case "villager": {
				mobMonster = new CustomEntityVillagerNPC(worldServer);
				break;
			}
			default:return null;
			}
			break;
		}
		default:return null;
		}
		mobMonster.TEMPinitiate(node);
		return mobMonster;
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
		HashMap<Integer, Ability> abilities = new HashMap<Integer, Ability>();
		
		PKAMob pkaMob = new PKAMob(mobName, attributes, maxHealth, damage, level, mobStrength, mobStance, mobType, rareItemInt, abilities);
		
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
