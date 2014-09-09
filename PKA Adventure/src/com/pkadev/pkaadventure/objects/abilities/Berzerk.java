package com.pkadev.pkaadventure.objects.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.PKAPlayer;

public class Berzerk implements Ability {

	private Player player;
	private PKAPlayer pkaPlayer;
	private int[] values;
	private int rarity;
	private String reference = "ability_berzerk";
	private String name = "Berzerk";
	
	@Override
	public void trigger(LivingEntity livingEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(PKAPlayer pkaPlayer, int[] values, int rarity) {
		this.player = player;
		this.pkaPlayer = pkaPlayer;
		this.values = values;
		this.rarity = rarity;
	}

	@Override
	public String getReference() {
		return reference;
	}

	@Override
	public int[] getValues() {
		return values;
	}

	@Override
	public int getRarity() {
		return rarity;
	}

	@Override
	public String getName() {
		return name;
	}

	
	
}
