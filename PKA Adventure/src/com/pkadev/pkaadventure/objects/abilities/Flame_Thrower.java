package com.pkadev.pkaadventure.objects.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.interfaces.Ability;

public class Flame_Thrower implements Ability {

	@Override
	public void trigger() {
		Bukkit.broadcastMessage("a");
	}

	@Override
	public void initialize(Player player, int[] values, int rarity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getReference() {
		return "ability_flame_thrower";
	}

	@Override
	public int[] getValues() {
		return new int[]{1};
	}

	@Override
	public int getRarity() {
		return 2;
	}

	@Override
	public String getName() {
		return "Flame Thrower";
	}
}
