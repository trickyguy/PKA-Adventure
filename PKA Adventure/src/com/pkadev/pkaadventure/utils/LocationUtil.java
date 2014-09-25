package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.SpawnNode;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.processors.SpawnNodeProcessor;
import com.pkadev.pkaadventure.types.MessageType;

public class LocationUtil {
	
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

	/**
	 * list of names of regions the player is currently in
	 * @param playerName
	 * @return
	 */
	public static List<String> getCurrentLocations(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
