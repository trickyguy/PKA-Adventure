package com.pkadev.pkaadventure.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.types.MessageType;

public class MessageUtil {
	private static final Main plugin = Main.instance;
	
	public static void sendMessage(Player player, String message, MessageType messageType) {
		String finalizedMessage = messageType.getFinalizedMessage(message);
		if (messageType == MessageType.SERVER || messageType == MessageType.SERVER_DEBUG)
			Bukkit.broadcastMessage(finalizedMessage);
		else if (messageType == MessageType.SINGLE || messageType == MessageType.SINGLE_DEBUG)
			player.sendMessage(finalizedMessage);
	}
	
	public static void log(String message) {
		plugin.log(message);
	}
	
	public static void severe(String message) {
		plugin.severe(message);
	}
	
	public static void d(int i) {
		sendMessage(null, "" + i, MessageType.SERVER_DEBUG);
	}
	
	public static void d(String s) {
		sendMessage(null, s, MessageType.SERVER_DEBUG);
	}
}
