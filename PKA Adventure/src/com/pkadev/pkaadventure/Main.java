package com.pkadev.pkaadventure;

import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.listeners.CombatListener;
import com.pkadev.pkaadventure.listeners.InventoryListener;
import com.pkadev.pkaadventure.listeners.JobListener;
import com.pkadev.pkaadventure.processors.CommandProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.processors.SpawnNodeProcessor;
import com.pkadev.pkaadventure.types.CustomEntityType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;

public class Main extends JavaPlugin {
	
	public static Main instance;
	public int derp;
	
	@Override
	public void onEnable() {
		instance = this;
		registerListeners();
		
		CustomEntityType.load();
		FileUtil.load();
		SpawnNodeProcessor.load();
		InventoryUtil.load();
		InventoryMain.loadShops();
		ItemUtil.load();
		
		getCommand("pka").setExecutor(CommandProcessor.i());
		
		PlayerProcessor.loadAllPlayers();
	}
	
	@Override
	public void onDisable() {
		CustomEntityType.unload();
	}
	
	public void registerListeners() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(JobListener.i(), this);
		pluginManager.registerEvents(CombatListener.i(), this);
		pluginManager.registerEvents(InventoryListener.i(), this);
	}
	
	/**
	 * will add prefix and log as info.
	 */
	public void log(String message) {
		getLogger().log(Level.INFO, message);
	}
	
	/**
	 * will add prefix and log as severe.
	 */
	public void severe(String message) {
		getLogger().severe(message);
	}
	
	/**
	 * disables PKAAdventure
	 */
	public void disable() {
		getServer().getPluginManager().disablePlugin(this);
	}
	
	/**
	 * used for getting stuff for the config.yml creation
	 */
	public InputStream getInputStream(String s){
		return this.getResource(s);
	}
}
