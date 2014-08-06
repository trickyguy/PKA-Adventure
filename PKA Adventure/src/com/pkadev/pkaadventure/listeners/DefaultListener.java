package com.pkadev.pkaadventure.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class DefaultListener {
	private Main plugin = Main.instance;

	private static DefaultListener i; private DefaultListener(){} public static DefaultListener i() {if (i == null)i = new DefaultListener();return i;}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerProcessor.loadPlayer(event.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		PlayerProcessor.unloadPlayer(event.getPlayer());
	}
}
