package com.pkadev.pkaadventure.utils;

import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.types.MessageType;

public class MessageUtil {

	public static void sendMessage(Player player, String message, MessageType messageType) {
		String finalizedMessage = messageType.getFinalizedMessage(message);
		player.sendMessage(finalizedMessage);
	}
	
}
