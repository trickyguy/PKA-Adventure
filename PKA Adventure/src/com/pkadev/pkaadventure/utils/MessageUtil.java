package com.pkadev.pkaadventure.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.types.MessageType;

public class MessageUtil {
	
	public static void sendMessage(Player player, String message, MessageType messageType) {
		String finalizedMessage = messageType.getFinalizedMessage(message);
		if (messageType == MessageType.SERVER || messageType == MessageType.SERVER_DEBUG)
			Bukkit.broadcastMessage(finalizedMessage);
		else if (messageType == MessageType.SINGLE || messageType == MessageType.SINGLE_DEBUG)
			player.sendMessage(finalizedMessage);
	}
	
	public static void d(int i) {
		sendMessage(null, "" + i, MessageType.SERVER_DEBUG);
	}
	
}
