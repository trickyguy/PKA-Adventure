package com.pkadev.pkaadventure.interfaces;

import org.bukkit.entity.Player;

public interface Ability {

	/**
	 * @param player
	 * @param params: depends on the ability being triggered, and will affect that ability in some way
	 */
	public void trigger();
	public void initialize(Player player, int[] values, int rarity);
	
	public String getReference();
	public int[] getValues();
	public int getRarity();
	public String getName();

}
