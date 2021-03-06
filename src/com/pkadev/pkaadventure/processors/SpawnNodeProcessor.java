package com.pkadev.pkaadventure.processors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.Navigation;
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
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityCaveSpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityEndermanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityGolemEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityPigmanEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySilverfishEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySkeletonEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntitySpiderEvil;
import com.pkadev.pkaadventure.objects.mobs.evil.CustomEntityZombieEvil;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityCaveSpiderGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityEndermanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityGolemGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityPigmanGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySilverfishGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySkeletonGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntitySpiderGood;
import com.pkadev.pkaadventure.objects.mobs.good.CustomEntityZombieGood;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityCaveSpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityEndermanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityGolemNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityPigmanNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySilverfishNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySkeletonNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntitySpiderNeutral;
import com.pkadev.pkaadventure.objects.mobs.neutral.CustomEntityZombieNeutral;
import com.pkadev.pkaadventure.objects.mobs.npc.CustomEntityVillagerNPC;
import com.pkadev.pkaadventure.objects.mobs.npc.CustomEntityZombieNPC;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.types.SpawnNodeType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MathUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class SpawnNodeProcessor {
	private static Main plugin = null;
	private static Random random = new Random();
	private static WorldServer worldServer = null;
	private static World world = null;
	
	//1 = mobs, 2 = beacons, 3 = lootcrate
	private static HashMap<SpawnNode, Integer> 	list1 = new HashMap<SpawnNode, Integer>();
	private static HashMap<String, SpawnNode> 	list2 = new HashMap<String, SpawnNode>();
	private static HashMap<SpawnNode, Integer> 	list3 = new HashMap<SpawnNode, Integer>();
	
	private static void addNodeToList1(SpawnNode node) {
		list1.put(node, -1);
	}
	private static void addNodeToList2(SpawnNode node) {
		list2.put(node.getName(), node);
	}
	private static void addNodeToList3(SpawnNode node) {
		list3.put(node, 5);
	}
	
	public static HashMap<String, SpawnNode> getBeaconList() {
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
	
	public static void load(Main instance) {
		plugin = instance;
		random = new Random();
		world = Bukkit.
				getWorld(
						FileUtil.
						getStringValueFromConfig(FileUtil.
								getConfig(), "homeworld", "config.yml"));
		worldServer = ((CraftWorld) world).getHandle();
		
		
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
		} else {
			MessageUtil.severe("Couldn't find any beacons, disabling plugin.");
			plugin.disable();
		}
		
		if (spawnNodeConfig.contains("Lootcrates")) {
			ConfigurationSection section = spawnNodeConfig.getConfigurationSection("Lootcrates");
			load(section, 3);
		}
		
		startTimer();
	}
	
	private static int load(ConfigurationSection section, int listNumber) {
		//create fileName mobName radius level amount mob strength stance
		int amount = 0;
		for (String nodeName : section.getKeys(false)) {
			SpawnNode node = null;
			if (listNumber == 1) {
				try {
					
					Location location = getLocation(section, nodeName);
					String name =section.getString(nodeName + ".name");
					int radius = section.getInt(nodeName + ".radius");
					int level = section.getInt(nodeName + ".level");
					int nodeAmount = section.getInt(nodeName + ".amount");
					String mob = section.getString(nodeName + ".mob");
					MobStrength mobStrength = MobStrength.valueOf(section.getString(nodeName + ".mobstrength").toUpperCase());
					MobStance mobStance = MobStance.valueOf(section.getString(nodeName + ".mobstance").toUpperCase());

					node = new SpawnNode(location, name, radius, level, nodeAmount, mob, mobStrength, mobStance);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageUtil.severe("critical loading error for spawnNode by fileName " + nodeName + ". It cannot be loaded!");
					continue;
				}
			} else if (listNumber == 2) {
				node = new SpawnNode(getLocation(section, nodeName), section.getString(nodeName + ".name"));
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
			if (mobMonster == null)
				return;
			initiateMobMonster(mobMonster, node);
			try {
				Field field = Navigation.class.getDeclaredField("e");
				field.setAccessible(true);
				AttributeInstance e = (AttributeInstance) field.get(mobMonster.getNavigation());
				e.setValue(32); // Navigation distance in block lengths goes here
				} catch (Exception ex) {
				}
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
		} else if (args.length == 7) {
			
			String name = args[0].replace('_', ' ');
			int radius = 1;
			int level = 1;
			int amount = 0;
			String mob = args[4];
			MobStrength mobStrength = MobStrength.ANIMAL;
			MobStance mobStance = MobStance.NEUTRAL;
			
			try {
				radius = Integer.parseInt(args[1]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Radius must be integer.");
				return false;
			}
			
			try {
				level = Integer.parseInt(args[2]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Level must be integer.");
				return false;
			}
			
			try {
				amount = Integer.parseInt(args[3]);
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("Amount must be integer.");
				return false;
			}
			
			try {
				mobStrength = MobStrength.valueOf(args[5].toUpperCase());
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("MobStrength " + args[5] + " does not exist.");
				return false;
			}
			
			try {
				mobStance = MobStance.valueOf(args[6].toUpperCase());
			} catch (IllegalArgumentException ex) {
				MessageUtil.severe("MobStance " + args[6] + " does not exist.");
				return false;
			}
			
			
			node = new SpawnNode(location, name, radius, level, amount, mob, mobStrength, mobStance);
			
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
			spawnNodeConfig.set(prefix + ".name",           node.getName());
		} else if (node.getSpawnNodeType() == SpawnNodeType.MOB) {
			prefix = "Mobs." + fileName;
			spawnNodeConfig.set(prefix + ".name", 			node.getName());
			spawnNodeConfig.set(prefix + ".radius", 		node.getRadius());
			spawnNodeConfig.set(prefix + ".level", 			node.getLevel());
			spawnNodeConfig.set(prefix + ".amount", 		node.getAmount());
			spawnNodeConfig.set(prefix + ".mob", 			node.getMob());
			spawnNodeConfig.set(prefix + ".mobstrength", 	node.getMobStrength().toString());
			spawnNodeConfig.set(prefix + ".mobstance", 		node.getMobStance().toString());
		}
		Location location = node.getLocation();
		spawnNodeConfig.set(prefix + ".location.x", 		(int) location.getX());
		spawnNodeConfig.set(prefix + ".location.y", 		(int) location.getY());
		spawnNodeConfig.set(prefix + ".location.z", 		(int) location.getZ());
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
				case "cavespider": {
					mobMonster = new CustomEntityCaveSpiderEvil(worldServer);
					break;
				}
				case "enderman": {
					mobMonster = new CustomEntityEndermanEvil(worldServer);
					break;
				}
				case "golem": {
					mobMonster = new CustomEntityGolemEvil(worldServer);
					break;
				}
				case "pigman": {
					mobMonster = new CustomEntityPigmanEvil(worldServer);
					break;
				}
				case "silverfish": {
					mobMonster = new CustomEntitySilverfishEvil(worldServer);
					break;
				}
				case "skeleton": {
					mobMonster = new CustomEntitySkeletonEvil(worldServer);
					break;
				}
				case "spider": {
					mobMonster = new CustomEntitySpiderEvil(worldServer);
					break;
				}
				case "zombie": {
					mobMonster = new CustomEntityZombieEvil(worldServer);
					break;
				}
			default:return null;
			}
			break;
		}
		case GOOD: {
			switch(node.getMob()) {
			case "cavespider": {
				mobMonster = new CustomEntityCaveSpiderGood(worldServer);
				break;
			}
			case "enderman": {
				mobMonster = new CustomEntityEndermanGood(worldServer);
				break;
			}
			case "golem": {
				mobMonster = new CustomEntityGolemGood(worldServer);
				break;
			}
			case "pigman": {
				mobMonster = new CustomEntityPigmanGood(worldServer);
				break;
			}
			case "silverfish": {
				mobMonster = new CustomEntitySilverfishGood(worldServer);
				break;
			}
			case "skeleton": {
				mobMonster = new CustomEntitySkeletonGood(worldServer);
				break;
			}
			case "spider": {
				mobMonster = new CustomEntitySpiderGood(worldServer);
				break;
			}
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
			case "cavespider": {
				mobMonster = new CustomEntityCaveSpiderNeutral(worldServer);
				break;
			}
			case "enderman": {
				mobMonster = new CustomEntityEndermanNeutral(worldServer);
				break;
			}
			case "golem": {
				mobMonster = new CustomEntityGolemNeutral(worldServer);
				break;
			}
			case "pigman": {
				mobMonster = new CustomEntityPigmanNeutral(worldServer);
				break;
			}
			case "silverfish": {
				mobMonster = new CustomEntitySilverfishNeutral(worldServer);
				break;
			}
			case "skeleton": {
				mobMonster = new CustomEntitySkeletonNeutral(worldServer);
				break;
			}
			case "spider": {
				mobMonster = new CustomEntitySpiderNeutral(worldServer);
				break;
			}
			case "zombie": {
				mobMonster = new CustomEntityZombieNeutral(worldServer);
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
				case "zombie": {
					mobMonster = new CustomEntityZombieNPC(worldServer);
					break;
				}
			default:return null;
			}
			break;
		}
		default:return null;
		}
		mobMonster.initiate(node);
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
		int rareItemInt = 			setInitialArmorContent(mobMonster, node);
		HashMap<Integer, Ability> abilities = new HashMap<Integer, Ability>();
		
		PKAMob pkaMob = new PKAMob(mobName, attributes, maxHealth, damage, level, mobStrength, mobStance, rareItemInt, abilities);
		
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
		
		if (!node.getMob().equals("zombie") && !node.getMob().equals("skeleton"))
			return rareItemInt;
		
		((LivingEntity) mobMonster.getEntity().getBukkitEntity()).getEquipment().setArmorContents(ItemUtil.getInitialContent(node.getLevel(), rareItemInt));
		return rareItemInt;
	}
}
