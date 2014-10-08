package com.pkadev.pkaadventure.utils;

import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.ChatMessage;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.EnumHoverAction;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.PKATeam;
import com.pkadev.pkaadventure.types.MessageType;

public class MessageUtil {
	private static final Main plugin = Main.instance;

	public static void sendMessage(Player player, String message, MessageType messageType) {
		String finalizedMessage = messageType.getFinalizedMessage(message);
		if (messageType == MessageType.SERVER || messageType == MessageType.SERVER_DEBUG)
			Bukkit.broadcastMessage(finalizedMessage);
		else if (messageType == MessageType.SINGLE || messageType == MessageType.SINGLE_DEBUG || messageType == MessageType.SIMPLE)
			if (player.isOnline())
				player.sendMessage(finalizedMessage);
	}

	public static void sendMessage(PKATeam pkaTeam, String message, MessageType messageType) {
		String finalizedMessage = messageType.getFinalizedMessage(message);
		if (messageType == MessageType.TEAM || messageType == MessageType.TEAM_DEBUG) {
			if (pkaTeam.getOnlinePlayers() != null) {
				for (PKAPlayer pkaPlayer : pkaTeam.getOnlinePlayers()) {
					pkaPlayer.getPlayer().sendMessage(finalizedMessage);
				}
			}
		}
	}

	public static void log(String message) {
		plugin.log(message);
	}

	public static void severe(String message) {
		plugin.severe(message);
	}

	public static void d(int i) {
		sendMessage(Bukkit.getPlayer(""), "" + i, MessageType.SERVER_DEBUG);
	}
	
	public static void d(String s) {
		sendMessage(Bukkit.getPlayer(""), s, MessageType.SERVER_DEBUG);
	}
	
	public static void d(int i, Object obj) {
		d(i, obj.getClass().getName());
	}

	public static void d(String s, Object obj) {
		d(s, obj.getClass().getName());
	}
	public static void d(int i, String s) {
		d((short) System.currentTimeMillis() + " " + i + " §cfrom " + s);
	}

	public static void d(String s, String d) {
		d((short) System.currentTimeMillis() + " " + s + " §cfrom " + d);
	}

	/**
	 * This method is used to display hoverable text in chat to a player.
	 * Use \n to have multiple lines in the hover message.
	 * @param player The player that you want to send the message to
	 * @param chatMessage The message that will be shown in chat
	 * @param clickableMessage What will be put in their text box upon clicking (can leave as null)
	 * @param hoverMessage Message that you want to display upon hovering
	 */
	public static void printHoverable(String playerName, String chatMessage, String clickableMessage, String hoverMessage) {
		IChatBaseComponent chatBase = new ChatMessage(chatMessage);

		chatBase.setChatModifier(new ChatModifier());
		chatBase.getChatModifier().setChatClickable(new ChatClickable(EnumClickAction.SUGGEST_COMMAND, clickableMessage));
		chatBase.getChatModifier().a(new ChatHoverable(EnumHoverAction.SHOW_TEXT, new ChatMessage(hoverMessage)));

		PlayerList list = MinecraftServer.getServer().getPlayerList();
		list.getPlayer(playerName).sendMessage(chatBase);
	}

	/**
	 * This is used to center text in chat using ChatPaginator.
	 * @param text The string that you want to center
	 * @return Padded string
	 */
	public static String centerText(String text) {
		String title = "";
		for(int x = 0; x <= ((ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH / 2) - ChatColor.stripColor(text).length()); x ++) {
			title += " ";
		} title += text;
		return title;
	}

}
