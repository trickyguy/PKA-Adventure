package com.pkadev.pkaadventure.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pkadev.pkaadventure.Main;

public class FileUtil {
	private static Main plugin = Main.instance;

	private static YamlConfiguration config = 			null;
	private static YamlConfiguration spawnNodeConfig = 	null;
	private static YamlConfiguration inventoryConfig = 	null;
	private static YamlConfiguration itemTypeConfig = 	null;
	private static YamlConfiguration nameConfig = 		null;
	private static YamlConfiguration dropConfig =    	null;

	/**
	 * has to be run when plugin loads
	 */
	public static void load() {
		File playerFolder = 	new File("plugins/PKAAdventure/players");
		File configFile = 		new File("plugins/PKAAdventure/config.yml");
		File spawnNodeFile = 	new File("plugins/PKAAdventure/spawnnodes.yml");
		File inventoryFile = 	new File("plugins/PKAAdventure/inventories.yml");
		File itemTypeFile = 	new File("plugins/PKAAdventure/itemtypes.yml");
		File nameFile = 		new File("plugins/PKAAdventure/names.yml");
		File dropFile = 		new File("plugins/PKAAdventure/drops.yml");
		
		if (!playerFolder.exists()) {
			MessageUtil.log("creating players folder");
			playerFolder.mkdirs();
		}

		if (!configFile.exists()) {
			loadDefaultConfig(configFile, 		"config.yml");
		}

		if (!spawnNodeFile.exists()) {
			loadDefaultConfig(spawnNodeFile, 	"spawnnodes.yml");
		}
		
		if (!inventoryFile.exists()) {
			loadDefaultConfig(inventoryFile, 	"inventories.yml");
		}
		
		if (!itemTypeFile.exists()) {
			loadDefaultConfig(itemTypeFile, 	"itemtypes.yml");
		}
		
		if (!nameFile.exists()) {
			loadDefaultConfig(nameFile, 		"names.yml");
		}
		
		if (!dropFile.exists()) {
			loadDefaultConfig(dropFile, 		"drops.yml");
		}

		MessageUtil.log("loaded all folders and config.yml");
		config = 				YamlConfiguration.loadConfiguration(configFile);
		spawnNodeConfig = 		YamlConfiguration.loadConfiguration(spawnNodeFile);
		inventoryConfig = 		YamlConfiguration.loadConfiguration(inventoryFile);
		itemTypeConfig = 		YamlConfiguration.loadConfiguration(itemTypeFile);
		nameConfig = 			YamlConfiguration.loadConfiguration(nameFile);
		dropConfig =			YamlConfiguration.loadConfiguration(dropFile);
	}

	public static YamlConfiguration reloadFile(File configFile, String configFileReference) {
		loadDefaultConfig(configFile, configFileReference);
		return YamlConfiguration.loadConfiguration(configFile);
	}

	/**
	 * @param configFile: configFile
	 * @param configFileReference: "config.yml"
	 */
	private static void loadDefaultConfig(File configFile, String configFileReference) {
		MessageUtil.log("creating " + configFileReference);
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			MessageUtil.severe("could not create " + configFileReference);
			e.printStackTrace();
			plugin.disable();
			return;
		}
		writeDefaultFile(configFile, configFileReference);
	}
	
	public static void save(YamlConfiguration config, String filepath) {
		File file = new File(filepath);
		try {
			config.save(file);
		} catch (IOException e) {
			MessageUtil.severe("could not save " + filepath);
			e.printStackTrace();
		}
	}

	public static YamlConfiguration getConfig() {
		return config;
	}

	public static YamlConfiguration getSpawnNodeConfig() {
		return spawnNodeConfig;
	}
	
	public static YamlConfiguration getInventoryConfig() {
		return inventoryConfig;
	}
	
	public static YamlConfiguration getItemTypeConfig() {
		return itemTypeConfig;
	}

	public static YamlConfiguration getNameConfig() {
		return nameConfig;
	}
	
	public static YamlConfiguration getDropConfig() {
		return dropConfig;
	}
	
	public static void saveConfig(YamlConfiguration config) {
		try {
			config.save(new File("plugins/PKAAdventure/config.yml"));
		} catch (IOException e) {
			MessageUtil.severe("could not save config.yml");
			e.printStackTrace();
		}
	}

	/**
	 * @param path possible paths: "MessageType.SINGLE.prefix for SINGLE, GROUP, SERVER, SINGLE_DEBUG, etc."
	 * @return
	 */
	public static String getStringValueFromConfig(YamlConfiguration config, String path, String configFileReference) {
		return getStringValueFromConfig(config, path, configFileReference, true);
	}

	private static String getStringValueFromConfig(YamlConfiguration config, String path, String configFileReference, boolean secondtry) {
		if (config.contains(path) && config.isString(path)) {
			return config.getString(path).replace('=', '§');
		} else {
			if (secondtry) {
				MessageUtil.severe("failed finding path: " + path + " in the default " + configFileReference + ", or is not String. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				MessageUtil.severe("could not find path: " + path + " in " + configFileReference + ", or is not String. Creating new " + configFileReference + " and trying again");
				File configFile = new File("plugins/PKAAdventure/" + configFileReference);
				return getStringValueFromConfig(reloadFile(configFile, configFileReference), path, configFileReference, true);
			}
		}
	}

	/**
	 * @param path possible paths: "MessageType.SINGLE.togglecolors for SINGLE, GORUP, SERVER, SINGLE_DEBUG, etc."
	 * @return
	 */
	public static List<String> getStringListFromConfig(YamlConfiguration config, String path, String configFileReference) {		
		return getStringListFromConfig(config, path, configFileReference, false);
	}
	
	/**
	 * used only when you expect there to maybe not be a value, but it doesn't matter
	 * @param config
	 * @param path
	 * @param configFileReference
	 * @return
	 */
	public static List<String> getStringListFromConfigSAFE(YamlConfiguration config, String path, String configFileReference) {
		if (config.contains(path) && config.isList(path))
			return getFinalizedStringList(config.getStringList(path));
		else {
			return null;
		}
	}
	
	private static List<String> getStringListFromConfig(YamlConfiguration config, String path, String configFileReference, boolean secondtry) {		
		if (config.contains(path) && config.isList(path))
			return getFinalizedStringList(config.getStringList(path));
		else {
			if (secondtry) {
				MessageUtil.severe("failed finding path: " + path + " in the default " + configFileReference + ", or is not List. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				MessageUtil.severe("could not find path: " + path + " in " + configFileReference + ", or is not List. Creating new " + configFileReference + " and trying again");
				File configFile = new File("plugins/PKAAdventure/" + configFileReference);
				return getStringListFromConfig(reloadFile(configFile, configFileReference), path, configFileReference, true);
			}

		}
		
	}

	/**
	 * @param path possible paths: "Math.skill_exp_yoffset"
	 * @return
	 */
	public static int getIntValueFromConfig(YamlConfiguration config, String path, String configFileReference) {
		return getIntValueFromConfig(config, path, configFileReference, false);
	}

	private static int getIntValueFromConfig(YamlConfiguration config, String path, String configFileReference, boolean secondtry) {
		if (config.contains(path) && config.isInt(path)) {
			return config.getInt(path);
		} else {
			if (secondtry) {
				MessageUtil.severe("failed finding path: " + path + " in the default " + configFileReference + ", or is not Integer. Disabling plugin, contact developer.");
				plugin.disable();
				return 0;
			} else {
				MessageUtil.severe("could not find path: " + path + " in " + configFileReference+ ", or is not Integer. Creating new " + configFileReference + " and trying again");
				File configFile = new File("plugins/PKAAdventure/" + configFileReference);
				return getIntValueFromConfig(reloadFile(configFile, configFileReference), path, configFileReference, true);
			}
		}
	}

	/**
	 * @param path possible paths: "Math.skill_exp_multiplier"
	 * @return
	 */
	public static double getDoubleValueFromConfig(YamlConfiguration config, String path, String configFileReference) {
		return getDoubleValueFromConfig(config, path, configFileReference, false);
	}

	private static double getDoubleValueFromConfig(YamlConfiguration config, String path, String configFileReference, boolean secondtry) {
		if (config.contains(path) && !config.equals(null)) {
			if(config.isDouble(path))
				return config.getDouble(path);
			else
				return 0.0;
		} else {
			if (secondtry) {
				MessageUtil.severe("failed finding path: " + path + " in the default " + configFileReference + ", or is not Double. Disabling plugin, contact developer.");
				plugin.disable();
				return 0.0;
			} else {
				MessageUtil.severe("could not find path: " + path + " in " + configFileReference + ", or is not Double. Creating new " + configFileReference + " and trying again");
				File configFile = new File("plugins/PKAAdventure/" + configFileReference);
				return getDoubleValueFromConfig(reloadFile(configFile, configFileReference), path, configFileReference, true);
			}
		}
	}
	
	public static int getListLengthFromConfig(YamlConfiguration config, String path, String configFileReference) {
		return getListLengthFromConfig(config, path, configFileReference, false);
	}
	
	private static int getListLengthFromConfig(YamlConfiguration config, String path, String configFileReference, boolean secondtry) {
		if (config.contains(path) && !config.equals(null)) {
			if(config.isConfigurationSection(path))
				return inventoryConfig.getConfigurationSection("Inventories.Food.Contents").getKeys(false).size();
			else
				return 0;
		} else {
			if (secondtry) {
				MessageUtil.severe("failed finding path: " + path + " in the default " + configFileReference + ", contact developer.");
				plugin.disable();
				return 0;
			} else {
				MessageUtil.severe("could not find path: " + path + " in " + configFileReference + ".");
				File configFile = new File("plugins/PKAAdventure/" + configFileReference);
				return getListLengthFromConfig(reloadFile(configFile, configFileReference), path, configFileReference, true);
			}
		}
	}
	
	private static List<String> getFinalizedStringList(List<String> list) {
		List<String> newList = new ArrayList<String>();
		for (String s : list) {
			newList.add(s.replace('=', '§'));
		}
		return newList;
	}
	
	public static void writeDefaultFile(File configFile, String defaultFileReference) {
		InputStream inputStream = plugin.getInputStream("PKA Adventure/" + defaultFileReference);

		if(inputStream == null){
			MessageUtil.severe("could not write defaultFileReference to file");
			return;
		}
		try{
			OutputStream outputStream = new FileOutputStream(configFile);

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
