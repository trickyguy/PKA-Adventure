package com.pkadev.pkaadventure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.pkadev.pkaadventure.listeners.CombatListener;
import com.pkadev.pkaadventure.listeners.DefaultListener;
import com.pkadev.pkaadventure.listeners.InventoryListener;
import com.pkadev.pkaadventure.listeners.JobListener;
import com.pkadev.pkaadventure.objects.PKAQuest;
import com.pkadev.pkaadventure.processors.CommandProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.processors.QuestProcessor;
import com.pkadev.pkaadventure.processors.SpawnNodeProcessor;
import com.pkadev.pkaadventure.types.CustomEntityType;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.LocationUtil;
import com.pkadev.pkaadventure.utils.SidebarUtil;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	@Override
	public void onEnable() {
		instance = this;
		registerListeners();
		
		FileUtil.load();
		CustomEntityType.load();
		SpawnNodeProcessor.load(instance);
		InventoryUtil.load();
		ItemUtil.load();
		ElementsUtil.load();
		QuestProcessor.load();
		LocationUtil.load(instance);
		SidebarUtil.load(instance);
		//setupDatabase();
		
		getCommand("pka").setExecutor(CommandProcessor.i());
		
		PlayerProcessor.loadAllPlayers();
	}
	
	@Override
	public void onDisable() {
		CustomEntityType.unload();
		
		instance = null;
	}
	
	public void registerListeners() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(CombatListener.i(), this);
		pluginManager.registerEvents(DefaultListener.i(), this);
		pluginManager.registerEvents(InventoryListener.i(), this);
		pluginManager.registerEvents(JobListener.i(), this);
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
	
	private void setupDatabase() {
    	try {
    		getDatabase().find(PKAQuest.class).findRowCount();
    	}
    	catch (PersistenceException ex) {
    		System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
    		installDDL();
    	}
    }
	
	@Override
    public List<Class<?>> getDatabaseClasses() {
    	List<Class<?>> list = new ArrayList<Class<?>>();
    	list.add(PKAQuest.class);
    	return list;
    }
}
