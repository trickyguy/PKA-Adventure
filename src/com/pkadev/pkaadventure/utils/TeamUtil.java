package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.PKATeam;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.MessageType;

public class TeamUtil {
	private static HashMap<String, PKATeam> teams = null;
	
	public static PKATeam getTeam(String teamName) {
		return teams.get(teamName);
	}
	
	/**
	 * Loads teams and puts all online players into their teams in online state.
	 * Used only on loadup
	 */
	public static void load() {
		YamlConfiguration config = FileUtil.getTeamConfig();
		teams = new HashMap<String, PKATeam>();
		
		if (config.contains("Teams")) {
			for (String name : config.getConfigurationSection("Teams").getKeys(false)) {
				if (name.equals("default_team_name"))
					continue;
				ConfigurationSection section = config.getConfigurationSection("Teams." + name);
				
				List<String> members = 			section.getStringList("members");
				String owner = 					section.getString("owner");
				List<String> admins = 			section.getStringList("admins");
				List<String> invitees = 		null;
				
				if (section.contains("invitees"))
					invitees = 					section.getStringList("invitees");
				
				PKATeam pkaTeam = null;
				
				if (invitees == null)
					pkaTeam = new PKATeam(members, name, owner, admins);
				else {
					pkaTeam = new PKATeam(members, name, owner, admins, invitees);
				}
				
				teams.put(name, pkaTeam);
			}
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadPlayer(player);
		}
	}
	
	public static void saveTeams() {
		YamlConfiguration config = FileUtil.getTeamConfig();
		if (!config.contains("Teams"))
			config.set("Teams.default_team_name", "This team cannot be used!");
		ConfigurationSection section = config.getConfigurationSection("Teams");
		
		for (String name : teams.keySet()) {
			PKATeam pkaTeam = getTeam(name);
			section.set(name + ".members", 	pkaTeam.getOfflinePlayers());
			section.set(name + ".owner", 	pkaTeam.getOwner());
			section.set(name + ".admins", 	pkaTeam.getAdmins());
			if (!(pkaTeam.getInvitees() == null) && pkaTeam.getInvitees().isEmpty())
				section.set(name + ".invitees", pkaTeam.getInvitees());
		}
		
		FileUtil.save(config, "plugins/PKAAdventure/teams.yml");
	}
	
	public static void loadPlayer(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null)
			return;
		pkaTeam.addOnlineMember(pkaPlayer);
	}
	
	public static void unloadPlayer(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null)
			return;
		pkaTeam.removeOnlineMember(pkaPlayer);
	}
	
	public static void create(final String playerName, String teamName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		if (teams.containsKey(teamName)) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "A team by that name already exists.", MessageType.SINGLE);
			return;
		}
		
		if (teamName.length() > 20) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "Maximum team name length is 20.", MessageType.SINGLE);
			return;
		}
		
		List<String> offlinePlayers = 	new ArrayList<String>(){{add(playerName);}};
		List<String> admins = 			new ArrayList<String>(){{add(playerName);}};
		
		PKATeam pkaTeam = new PKATeam(offlinePlayers, teamName, playerName, admins);
		pkaTeam.addOnlineMember(pkaPlayer);
		pkaPlayer.setPKATeam(pkaTeam);
		teams.put(teamName, pkaTeam);
		
		MessageUtil.sendMessage(pkaPlayer.getPlayer(), "The team has been created.", MessageType.SINGLE);
	}
	
	/**
	 * adminName is the name of the person that is trying to invite, he doesnt have to be admin, nor does he have to be in team
	 * @param playerName
	 * @param adminName
	 */
	public static void invite(String playerName, String adminName) {
		PKAPlayer admin = PlayerProcessor.getPKAPlayer(adminName);
		if (admin == null)
			return;
		
		PKATeam pkaTeam = admin.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(admin.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (playerName.equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Seriously?", MessageType.SINGLE);
			return;
		}
		
		if (!FileUtil.playerFileExists(playerName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "That player does not exist, or has never joined before", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getOfflinePlayers().contains(playerName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "That player is already in the team.", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getAdmins().contains(adminName))
			MessageUtil.sendMessage(admin.getPlayer(), "You do not have permission to do that.", MessageType.SINGLE);
		else {
			pkaTeam.addInvitee(playerName);
			MessageUtil.sendMessage(admin.getPlayer(), "You have invited " + playerName + " to the group.", MessageType.SINGLE);
			MessageUtil.sendMessage(Bukkit.getPlayer(playerName), "You were invited to join the team " + pkaTeam.getName() + " by " + adminName, MessageType.SINGLE);
			MessageUtil.sendMessage(Bukkit.getPlayer(playerName), "/team accept " + pkaTeam.getName(), MessageType.SINGLE);
		}
	}
	
	public static void deinvited(String playerName, String adminName) {
		PKAPlayer admin = PlayerProcessor.getPKAPlayer(adminName);
		if (admin == null)
			return;
		
		PKATeam pkaTeam = admin.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(admin.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (playerName.equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Seriously?", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getOfflinePlayers().contains(playerName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "That player is already in the team, too late.", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getAdmins().contains(adminName))
			MessageUtil.sendMessage(admin.getPlayer(), "You do not have permission to do that.", MessageType.SINGLE);
		else {
			if (!pkaTeam.getInvitees().contains(playerName))
				MessageUtil.sendMessage(admin.getPlayer(), "This person was never invited.", MessageType.SINGLE);
			else {
				pkaTeam.removeInvitee(playerName);
				MessageUtil.sendMessage(admin.getPlayer(), "You have un-invited " + playerName + " from the group.", MessageType.SINGLE);
			}
		}
	}
	
	public static void acceptInvite(String playerName, String teamName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		PKATeam pkaTeam = getTeam(teamName);
		if (pkaTeam == null) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "That team doesn't exist, and maybe never has...", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getOfflinePlayers().contains(playerName)) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You are already in the team, numbnuts.", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getInvitees().contains(playerName))
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You don't have an open invite for this team.", MessageType.SINGLE);
		else {
			pkaTeam.removeInvitee(playerName);
			pkaPlayer.setPKATeam(pkaTeam);
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You have joined " + teamName + ".", MessageType.SINGLE);
			MessageUtil.sendMessage(pkaTeam, playerName + " has joined the team.", MessageType.TEAM);
			pkaTeam.addMember(pkaPlayer);
		}
	}
	
	public static void leaveTeam(String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getOwner().equals(playerName)) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "Disband the team if you wish to leave it.", MessageType.SINGLE);
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "/team disband", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getAdmins().contains(playerName)) {
			pkaTeam.removeAdmin(playerName);
			pkaTeam.removeMember(pkaPlayer);
		} else {
			pkaTeam.removeMember(pkaPlayer);
		}
		
		pkaPlayer.setPKATeam(null);
		MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You have left the team.", MessageType.SINGLE);
		MessageUtil.sendMessage(pkaTeam, playerName + " has left the team.", MessageType.TEAM);
	}
	
	/**
	 * 
	 * @param playerName
	 * @param adminName: could be anyone. Members cant kick admins, admins can kick members, admins cant kick admins, owner can kick anyone (not himself)
	 */
	public static void kickFromTeam(String playerName, String adminName) {
		PKAPlayer admin = PlayerProcessor.getPKAPlayer(adminName);
		if (admin == null)
			return;
		
		PKATeam pkaTeam = admin.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(admin.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (playerName.equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Or just use /pka leave. I'll do it for you.", MessageType.SINGLE);
			leaveTeam(playerName);
			return;
		}
		
		if (!pkaTeam.getAdmins().contains(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "You don't have permission to do this.", MessageType.SINGLE);
			return;
		}
		
		if (pkaTeam.getAdmins().contains(playerName) && !pkaTeam.getOwner().equals(adminName))
			MessageUtil.sendMessage(admin.getPlayer(), "Only the owner can kick admins from the team.", MessageType.SINGLE);
		else {
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
			if (pkaPlayer == null)
				pkaTeam.removeMember(playerName);
			else {
				pkaTeam.removeMember(pkaPlayer);
				MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You have been kicked from the team.", MessageType.SINGLE);
			}
			pkaPlayer.setPKATeam(null);
			MessageUtil.sendMessage(pkaTeam, playerName + " has been kicked from the team.", MessageType.TEAM);
		}
	}
	
	public static void disband(String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getOwner().equals(playerName)) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "Only the owner can disband the team.", MessageType.SINGLE);
			return;
		}
		
		MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You've disbanded the team.", MessageType.SINGLE);
		MessageUtil.sendMessage(pkaTeam, "The team has been disbanded.", MessageType.TEAM);
		
		for (PKAPlayer teamMember : pkaTeam.getOnlinePlayers()) {
			teamMember.setPKATeam(null);
		}
		pkaTeam.clear();
		teams.remove(pkaTeam);
	}
	
	public static void promote(String playerName, String adminName) {
		PKAPlayer admin = PlayerProcessor.getPKAPlayer(adminName);
		if (admin == null)
			return;
		
		PKATeam pkaTeam = admin.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(admin.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (playerName.equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Seriously?", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getOwner().equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Only the owner can promote/demote.", MessageType.SINGLE);
			return;
		}
			
		if (pkaTeam.getAdmins().contains(playerName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "He is already an admin.", MessageType.SINGLE);
			return;
		}
		
		pkaTeam.addAdmin(playerName);
		MessageUtil.sendMessage(admin.getPlayer(), "You've promoted " + playerName + ".", MessageType.SINGLE);
	}
	
	public static void demote(String playerName, String adminName) {
		PKAPlayer admin = PlayerProcessor.getPKAPlayer(adminName);
		if (admin == null)
			return;
		
		PKATeam pkaTeam = admin.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(admin.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
		
		if (playerName.equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Seriously?", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getOwner().equals(adminName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "Only the owner can promote/demote.", MessageType.SINGLE);
			return;
		}
		
		if (!pkaTeam.getAdmins().contains(playerName)) {
			MessageUtil.sendMessage(admin.getPlayer(), "He is not an admin.", MessageType.SINGLE);
			return;
		}
		
		pkaTeam.removeAdmin(playerName);
		MessageUtil.sendMessage(admin.getPlayer(), "You've demoted " + playerName + ".", MessageType.SINGLE);
	}
	
	public static void teamInfo(String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "You are currently not part of a team ;(", MessageType.SINGLE);
			return;
		}
	
		teamInfo(pkaPlayer, pkaTeam, true);
	}
	
	public static void teamInfo(String playerName, String teamName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return;
		
		PKATeam pkaTeam = getTeam(teamName);
		if (pkaTeam == null) {
			MessageUtil.sendMessage(pkaPlayer.getPlayer(), "That team does not exist.", MessageType.SINGLE);
			return;
		}
		
		teamInfo(pkaPlayer, pkaTeam, false);
	}
	
	private static void teamInfo(PKAPlayer pkaPlayer, PKATeam pkaTeam, boolean giveLocationInfo) {
		Player player = pkaPlayer.getPlayer();
		String ownerName = pkaTeam.getOwner();
		
		MessageUtil.sendMessage(player, "TeamName: " + pkaTeam.getName(), MessageType.SINGLE);
		MessageUtil.sendMessage(player, "Owner: " + pkaTeam.getOwner(), MessageType.SINGLE);
		
		if (pkaTeam.getAdmins().size() > 1) {
			MessageUtil.sendMessage(player, "Admins: " + pkaTeam.getOwner(), MessageType.SINGLE);
			for (String adminName : pkaTeam.getAdmins()) {
				if (!adminName.equals(ownerName)) {
					if (giveLocationInfo) {
						PKAPlayer offlinePlayer = PlayerProcessor.getPKAPlayer(adminName);
						if (offlinePlayer != null) {
							MessageUtil.sendMessage(player, " - " + adminName + " (" + offlinePlayer.getPlayer().getLocation().getX() + "," + offlinePlayer.getPlayer().getLocation().getZ() + ")" , MessageType.SINGLE);
							continue;
						}
					}
					MessageUtil.sendMessage(player, " - " + adminName, MessageType.SINGLE);
				}
			}
		}
		
		if (pkaTeam.getAdmins().size() > 1) {
			MessageUtil.sendMessage(player, "Members: " + pkaTeam.getOwner(), MessageType.SINGLE);
			for (String offlinePlayerName : pkaTeam.getOfflinePlayers()) {
				if (!offlinePlayerName.equals(ownerName) && !pkaTeam.getAdmins().contains(offlinePlayerName)) {
					if (giveLocationInfo) {
						PKAPlayer offlinePlayer = PlayerProcessor.getPKAPlayer(offlinePlayerName);
						if (offlinePlayer != null) {
							MessageUtil.sendMessage(player, " - " + offlinePlayerName + " (" + offlinePlayer.getPlayer().getLocation().getX() + "," + offlinePlayer.getPlayer().getLocation().getZ() + ")" , MessageType.SINGLE);
							continue;
						}
					}
					MessageUtil.sendMessage(player, " - " + offlinePlayerName, MessageType.SINGLE);
				}
			}
		}
	}
	
}
