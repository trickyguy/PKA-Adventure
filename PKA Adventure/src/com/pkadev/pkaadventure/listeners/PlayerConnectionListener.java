package com.pkadev.pkaadventure.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class PlayerConnectionListener implements Listener {
	Main plugin = Main.instance;

	private static PlayerConnectionListener i;
	private PlayerConnectionListener() {
		
	}

	public static PlayerConnectionListener i() {
		if (i == null)
			i = new PlayerConnectionListener();
		return i;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerProcessor.loadPlayer(player);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerProcessor.unloadPlayer(player);
	}
	
}
