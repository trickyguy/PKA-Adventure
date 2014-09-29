package com.pkadev.pkaadventure.objects;

import java.util.List;

import org.bukkit.entity.Player;

public class PKATeam {

	private List<Player> players = null;
	private String name = "default_team_name"; //the _ are so you know its serious #money
	
	public PKATeam(List<Player> players, String name) {
		
	}
	
	
	
	private List<Player> getPlayers() {
		return players;
	}

	private void setPlayers(List<Player> players) {
		this.players = players;
	}

	private String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}



	private static void saveTeam() {
		
	}
	
}
