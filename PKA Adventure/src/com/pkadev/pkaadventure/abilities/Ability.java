package com.pkadev.pkaadventure.abilities;

import org.bukkit.entity.Player;

public interface Ability {

	/**
	 * @param player
	 * @param params: depends on the ability being triggered, and will affect that ability in some way
	 */
	public void trigger(Player player, int[] params);
	public void remove(String playerName);
	
}
