package com.pkadev.pkaadventure.objects;

import java.util.List;

public class PKATeam {

	private List<PKAPlayer> players = null;
	private String name = "default_team_name"; //the _ are so you know its serious #money
	
	public PKATeam(List<PKAPlayer> players, String name) {
		setPlayers(players);
		setName(name);
		PKAPlayer pkaPlayer = null;
	}
	
	
	
	public List<PKAPlayer> getPlayers() {
		return players;
	}

	private void setPlayers(List<PKAPlayer> players) {
		this.players = players;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}



	private static void saveTeam() {
		
	}
	
}
