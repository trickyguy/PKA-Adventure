package com.pkadev.pkaadventure.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomStroll;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.mobs.CustomEntityZombieGood;

public class FileUtil {
	private static Main plugin = Main.instance;

	public static YamlConfiguration config = null;
	public static YamlConfiguration spawnNodeConfig = null;
	public static YamlConfiguration inventoryConfig = null;

	/**
	 * has to be run when plugin loads
	 */
	public static void load() {
		File playerFolder = new File("plugins/PKAAdventure/players");
		File configFile = new File("plugins/PKAAdventure/config.yml");
		File spawnNodeFile = new File("plugins/PKAAdventure/spawnnodes.yml");
		File inventoryFile = new File("plugins/PKAAdventure/inventories.yml");

		if (!playerFolder.exists()) {
			plugin.log("creating players folder");
			playerFolder.mkdirs();
		}

		if (!configFile.exists()) {
			plugin.log("creating config.yml");
			loadDefaultConfig(configFile);
		}

		if (!spawnNodeFile.exists()) {
			plugin.log("creating spawnnodes.yml");
			loadDefaultSpawnNodeFile(spawnNodeFile);
		}
		
		if (!inventoryFile.exists()) {
			plugin.log("creating inventories.yml");
			loadDefaultInventoryFile(inventoryFile);
		}

		plugin.log("loaded all folders and config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		spawnNodeConfig = YamlConfiguration.loadConfiguration(spawnNodeFile);
		inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);
	}

	public static void reloadFile(YamlConfiguration file) {
		File configFile = new File("plugins/PKAAdventure/" + file.getName() + ".yml");
		switch (file.getName()) {
		default:
			plugin.severe("Could reload " + file.getName() + ".yml");
			return;
		case "config":
			loadDefaultConfig(configFile);
			break;
		case "spawnnodes":
			loadDefaultSpawnNodeFile(configFile);
			break;
		case "inventories":
			loadDefaultInventoryFile(configFile);
			break;
		}
		file = YamlConfiguration.loadConfiguration(configFile);
	}
	
	/**
	 * used to reset the config.yml
	 */
	public static void reloadDefaultConfig() {
		File configFile = new File("plugins/PKAAdventure/config.yml");
		loadDefaultConfig(configFile);
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	private static void loadDefaultConfig(File configFile) {
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			plugin.severe("could not create config.yml");
			e.printStackTrace();
			plugin.disable();
			return;
		}
		writeDefaultFile(configFile, "config.yml");
	}

	private static void loadDefaultSpawnNodeFile(File spawnNodeFile) {
		try {
			spawnNodeFile.createNewFile();
		} catch (IOException e) {
			plugin.severe("could not create spawnNodeConfig.yml");
			e.printStackTrace();
			plugin.disable();
			return;
		}
		writeDefaultFile(spawnNodeFile, "spawnnodes.yml");
	}

	private static void loadDefaultInventoryFile(File inventoryFile) {
		try {
			inventoryFile.createNewFile();
		} catch (IOException e) {
			plugin.severe("could not create inventories.yml");
			e.printStackTrace();
			plugin.disable();
			return;
		}
		writeDefaultFile(inventoryFile, "inventories.yml");
	}
	
	public static void save(YamlConfiguration config, String filepath) {
		File file = new File(filepath);
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.severe("could not save " + filepath);
			e.printStackTrace();
		}
	}

	public static YamlConfiguration getConfig() {
		return config;
	}

	public static YamlConfiguration getSpawnNodeConfig() {
		return spawnNodeConfig;
	}

	public static void saveConfig(YamlConfiguration config) {
		try {
			config.save(new File("plugins/PKAAdventure/config.yml"));
		} catch (IOException e) {
			plugin.severe("could not save config.yml");
			e.printStackTrace();
		}
	}

	/**
	 * @param path possible paths: "MessageType.SINGLE.prefix for SINGLE, GROUP, SERVER, SINGLE_DEBUG, etc."
	 * @return
	 */
	public static String getStringValueFromConfig(YamlConfiguration file, String path) {
		return getStringValueFromConfig(file, path, true);
	}

	private static String getStringValueFromConfig(YamlConfiguration file, String path, boolean secondtry) {
		if (file.contains(path) && file.isString(path)) {
			return file.getString(path);
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default " + file.getName() + ", or is not String. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				plugin.severe("could not find path: " + path + " in " + file.getName() + ", or is not String. Creating new " + file.getName() + " and trying again");
				reloadFile(file);
				return getStringValueFromConfig(file, path, true);
			}
		}
	}

	/**
	 * @param path possible paths: "MessageType.SINGLE.togglecolors for SINGLE, GORUP, SERVER, SINGLE_DEBUG, etc."
	 * @return
	 */
	public static String[] getStringArrayFromConfig(YamlConfiguration file, String path) {		
		return getStringArrayFromConfig(file, path, false);
	}
	
	private static String[] getStringArrayFromConfig(YamlConfiguration file, String path, boolean secondtry) {		
		List<String> stringList = null;
		if (file.contains(path) && file.isList(path))
			stringList = file.getStringList(path);
		else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default " + file.getName() + ", or is not List. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				plugin.severe("could not find path: " + path + " in " + file.getName() + ", or is not List. Creating new " + file.getName() + " and trying again");
				reloadFile(file);
				return getStringArrayFromConfig(file, path, true);
			}

		}
		int stringListSize = stringList.size();
		String[] stringArray = new String[stringListSize];

		for (int i = 0; i < stringListSize; i++) {
			stringArray[i] = stringList.get(i);
		}

		return stringArray;
	}

	/**
	 * @param path possible paths: "Math.skill_exp_yoffset"
	 * @return
	 */
	public static int getIntValueFromConfig(YamlConfiguration file, String path) {
		return getIntValueFromConfig(file, path, false);
	}

	private static int getIntValueFromConfig(YamlConfiguration file, String path, boolean secondtry) {
		if (file.contains(path) && file.isInt(path)) {
			return file.getInt(path);
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default " + file.getName() + ", or is not Integer. Disabling plugin, contact developer.");
				plugin.disable();
				return 0;
			} else {
				plugin.severe("could not find path: " + path + " in " + file.getName() + ", or is not Integer. Creating new " + file.getName() + " and trying again");
				reloadFile(file);
				return getIntValueFromConfig(file, path, true);
			}
		}
	}

	/**
	 * @param path possible paths: "Math.skill_exp_multiplier"
	 * @return
	 */
	public static double getDoubleValueFromConfig(YamlConfiguration file, String path) {
		return getDoubleValueFromConfig(file, path, false);
	}

	private static double getDoubleValueFromConfig(YamlConfiguration file, String path, boolean secondtry) {
		if (file.contains(path) && !file.equals(null)) {
			if(file.isDouble(path))
				return file.getDouble(path);
			else
				return 0.0;
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default " + file.getName() + ", or is not Double. Disabling plugin, contact developer.");
				plugin.disable();
				return 0.0;
			} else {
				plugin.severe("could not find path: " + path + " in " + file.getName() + ", or is not Integer. Double new " + file.getName() + " and trying again");
				reloadFile(file);
				return getDoubleValueFromConfig(file, path, true);
			}
		}
	}
	
	public static void writeDefaultFile(File file, String defaultFileReference) {
		InputStream inputStream = plugin.getInputStream(defaultFileReference);

		if(inputStream == null){
			plugin.severe("could not write default player.yml to file");
			return;
		}
		try{
			OutputStream outputStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while((read = inputStream.read(bytes)) != -1){
				outputStream.write(bytes, 0, read);
			}
			inputStream.close();
			outputStream.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static YamlConfiguration getPlayerConfig(String playerName) {
		File playerFile = new File("plugins/PKAAdventure/players/" + playerName + ".yml");
		if (!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			writeDefaultFile(playerFile, "defaultplayer.yml");
			return YamlConfiguration.loadConfiguration(playerFile);
		} else {
			return YamlConfiguration.loadConfiguration(playerFile);
		}
	}

}
