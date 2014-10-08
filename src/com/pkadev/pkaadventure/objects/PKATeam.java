package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scoreboard.Team;

public class PKATeam {

	private List<PKAPlayer> onlinePlayers = null;
	private List<String> offlinePlayers = null; //all members, even the ones who are online
	private String name = "default_team_name"; //the _ are so you know its serious #money
	
	private String owner = null;
	private List<String> admins = null;
	private List<String> invitees = null;
	
	private Team scoreBoardTeam = null;
	
	/**
	 * @param offlinePlayers
	 * @param name
	 * @param owner
	 * @param admins
	 */
	public PKATeam(List<String> offlinePlayers, String name, final String owner, List<String> admins) {
		setOnlinePlayers(new ArrayList<PKAPlayer>());
		setOfflinePlayers(offlinePlayers);
		setName(name);
		setOwner(owner);
		setAdmins(admins);
		setInvitees(new ArrayList<String>());
	}
	
	/**
	 * @param offlinePlayers
	 * @param name
	 * @param owner
	 * @param admins
	 * @param invitees
	 */
	public PKATeam(List<String> offlinePlayers, String name, final String owner, List<String> admins, List<String> invitees) {
		setOnlinePlayers(new ArrayList<PKAPlayer>());
		setOfflinePlayers(offlinePlayers);
		setName(name);
		setOwner(owner);
		setAdmins(admins);
		setAdmins(admins);
		setInvitees(invitees);
	}
	
	public List<PKAPlayer> getOnlinePlayers() {
		return onlinePlayers;
	}

	private void setOnlinePlayers(List<PKAPlayer> onlinePlayers) {
		this.onlinePlayers = onlinePlayers;
	}
	
	public List<String> getOfflinePlayers() {
		return offlinePlayers;
	}
	
	private void setOfflinePlayers(List<String> offlinePlayers) {
		this.offlinePlayers = offlinePlayers;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	private void setOwner(String owner) {
		this.owner = owner;
	}

	public List<String> getAdmins() {
		return admins;
	}

	private void setAdmins(List<String> admins) {
		this.admins = admins;
	}

	public List<String> getInvitees() {
		return invitees;
	}

	private void setInvitees(List<String> invitees) {
		this.invitees = invitees;
	}

	//adminName doesnt have to be admin, you check if it is
	public void addInvitee(String playerName) {
		invitees.add(playerName);
	}
	
	public void removeInvitee(String playerName) {
		invitees.remove(playerName);
	}
	
	public void addMember(PKAPlayer pkaPlayer) {
		onlinePlayers.add(pkaPlayer);
		offlinePlayers.add(pkaPlayer.getName());
	}
	
	public void removeMember(PKAPlayer pkaPlayer) {
		if (onlinePlayers.contains(pkaPlayer))
			onlinePlayers.remove(pkaPlayer);
		removeMember(pkaPlayer.getName());
	}
	
	public void removeMember(String playerName) {
		offlinePlayers.remove(playerName);
		if (admins.contains(playerName))
			admins.remove(playerName);
	}
	
	public void addAdmin(String playerName) {
		admins.add(playerName);
	}
	
	public void removeAdmin(String playerName) {
		admins.remove(playerName);
	}
	
	public void addOnlineMember(PKAPlayer pkaPlayer) {
		onlinePlayers.add(pkaPlayer);
	}
	
	public void removeOnlineMember(PKAPlayer pkaPlayer) {
		onlinePlayers.remove(pkaPlayer);
	}
	
	public Team getScoreBoardTeam() {
		return scoreBoardTeam;
	}

	public void setScoreBoardTeam(Team scoreBoardTeam) {
		this.scoreBoardTeam = scoreBoardTeam;
	}

	public void clear() {
		onlinePlayers = null;
		offlinePlayers = null;
		name = null;
		owner = null;
		admins = null;
		invitees = null;
	}
	
}
