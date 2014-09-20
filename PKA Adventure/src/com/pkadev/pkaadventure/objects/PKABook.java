package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PKABook {

	List<PKAPage> pages = new ArrayList<PKAPage>();
	Player owner = null;
	
	public PKABook(Player player, List<PKAPage> pages) {
		owner = player;
		this.pages = pages;
	}
	
	
	
}
