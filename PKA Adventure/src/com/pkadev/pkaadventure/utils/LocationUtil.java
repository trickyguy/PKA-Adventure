package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.processors.SpawnNodeProcessor;
import com.pkadev.pkaadventure.types.MessageType;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class LocationUtil {
	private static Main plugin = null;
	private static WorldGuardPlugin guard = null;
	private static RegionManager manager = null;
	private static World world = null;
	
	public static void load(Main instance) {
		plugin = instance;
		String worldName = FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml");
		if (worldName == null)
			return;
		world = Bukkit.getWorld(worldName);
	}
	
	private static WorldGuardPlugin getWorldGuard() {
		if (guard != null)
			return guard;
		Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (worldGuardPlugin == null || !(worldGuardPlugin instanceof WorldGuardPlugin)) {
			MessageUtil.severe("the plugin WorldGuard could not be loaded. Disabling plugin!");
			plugin.disable();
			return null;
		}
		
		guard = (WorldGuardPlugin) worldGuardPlugin;
		manager = guard.getRegionManager(world);
			
		return guard;
	}
	
	public static void teleportToBeacon(Player player, boolean instant) {
		SpawnNode beacon = getBestAvailableBeacon(player);
		if (instant) {
			teleport(player, beacon);
		} else {
			teleportDelayed(player, beacon, (long) MathUtil.getInt("tp_tick_delay"));
		}
	}
	
	public static void teleport(Player player, SpawnNode beacon) {
		player.teleport(beacon.getLocation());
		MessageUtil.sendMessage(player, "You have teleported to " + beacon.getName(), MessageType.SINGLE);
	}
	
	public static void teleportDelayed(final Player player, final SpawnNode beacon, long delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {

			@Override
			public void run() {
				teleport(player, beacon);
			}
			
		}, delay);
	}
	
	private static SpawnNode getBestAvailableBeacon(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return null;
		
		HashMap<String, SpawnNode> beaconList = SpawnNodeProcessor.getBeaconList();
		Location location = player.getLocation();
		SpawnNode bestBeacon = beaconList.get("default");
		double shortestDistanceSquared = 0d;
		for (String locationName : pkaPlayer.getDiscoveredLocations()) {
			if (beaconList.containsKey(locationName)) {
				SpawnNode beacon = beaconList.get(locationName);
				if (pkaPlayer.getLevel() >= beacon.getLevel()) {
					
					double distanceSquared = beacon.getLocation().distanceSquared(location);
					
					if ((distanceSquared < shortestDistanceSquared) ||
							shortestDistanceSquared == 0d) {
						shortestDistanceSquared = distanceSquared;
						bestBeacon = beacon;
					}
				}
			}
		}
		return bestBeacon;
	}
	
	public static boolean isWithinRegion(Player player, String regionName) {
		if (!player.isOnline())
			return false;
		return isWithinRegion(player.getLocation(), regionName);
	}
	
	private static boolean isWithinRegion(Location location, String regionName) {
		WorldGuardPlugin guard = getWorldGuard();
		if (guard == null)
			return false;
		ApplicableRegionSet regionSet = manager.getApplicableRegions(location);
		for (ProtectedRegion region : regionSet) {
			if (region.getId() == regionName)
				return true;
		}
		return false;
	}
	
}
