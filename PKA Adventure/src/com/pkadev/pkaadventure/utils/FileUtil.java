package com.pkadev.pkaadventure.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.pkadev.pkaadventure.Main;

public class FileUtil {
	private static Main plugin = Main.instance;
	
	private static YamlConfiguration config = null;
	private static YamlConfiguration spawnNodeConfig = null;
	
	/**
	 * has to be run when plugin loads
	 */
	public static void load() {
		File playerFolder = new File("plugins/PKAAdventure/players");
		File configFile = new File("plugins/PKAAdventure/config.yml");
		File spawnNodeFile = new File("plugins/PKAAdventure/spawnnodes.yml");
		
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
		
		plugin.log("loaded all folders and config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		spawnNodeConfig = YamlConfiguration.loadConfiguration(spawnNodeFile);
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
	public static String getStringValueFromConfig(String path) {
		return getStringValueFromConfig(path, true);
	}
	
	private static String getStringValueFromConfig(String path, boolean secondtry) {
		if (config.contains(path) && config.isString(path)) {
			return config.getString(path);
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default config.yml, or is not String. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				plugin.severe("could not find path: " + path + " in config.yml, or is not String. Creating new config.yml and trying again");
				reloadDefaultConfig();
				return getStringValueFromConfig(path, true);
			}
		}
	}
	
	/**
	 * @param path possible paths: "MessageType.SINGLE.togglecolors for SINGLE, GORUP, SERVER, SINGLE_DEBUG, etc."
	 * @return
	 */
	public static String[] getStringArrayFromConfig(String path) {		
		return getStringArrayFromConfig(path, false);
	}
	
	private static String[] getStringArrayFromConfig(String path, boolean secondtry) {		
		List<String> stringList = null;
		if (config.contains(path) && config.isList(path))
			stringList = config.getStringList(path);
		else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default config.yml, or is not List. Disabling plugin, contact developer.");
				plugin.disable();
				return null;
			} else {
				plugin.severe("could not find path: " + path + " in config.yml, or is not List. Creating new config.yml and trying again");
				reloadDefaultConfig();
				return getStringArrayFromConfig(path, true);
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
	public static int getIntValueFromConfig(String path) {
		return getIntValueFromConfig(path, false);
	}
	
	private static int getIntValueFromConfig(String path, boolean secondtry) {
		if (config.contains(path) && config.isInt(path)) {
			return config.getInt(path);
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default config.yml, or is not String. Disabling plugin, contact developer.");
				plugin.disable();
				return 0;
			} else {
				plugin.severe("could not find path: " + path + " in config.yml, or is not String. Creating new config.yml and trying again");
				reloadDefaultConfig();
				return getIntValueFromConfig(path, true);
			}
		}
	}
	
	/**
	 * @param path possible paths: "Math.skill_exp_multiplier"
	 * @return
	 */
	public static double getDoubleValueFromConfig(String path) {
		return getDoubleValueFromConfig(path, false);
	}
	
	private static double getDoubleValueFromConfig(String path, boolean secondtry) {
		if (config.contains(path) && config.isDouble(path)) {
			return config.getDouble(path);
		} else {
			if (secondtry) {
				plugin.severe("failed finding path: " + path + " in the default config.yml, or is not String. Disabling plugin, contact developer.");
				plugin.disable();
				return 0;
			} else {
				plugin.severe("could not find path: " + path + " in config.yml, or is not String. Creating new config.yml and trying again");
				reloadDefaultConfig();
				return getDoubleValueFromConfig(path, true);
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
