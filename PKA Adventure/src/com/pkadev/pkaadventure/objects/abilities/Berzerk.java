package com.pkadev.pkaadventure.objects.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.PKAPlayer;

public class Berzerk implements Ability {

	//values[0] = noDamageTicks multiplier 2 or 3
	//values[1] = seconds
	
	private PKAPlayer pkaPlayer;
	private int[] values;
	private int rarity;
	private String reference = "ability_berzerk";
	private String name = "Berzerk";
	
	@Override
	public void trigger(LivingEntity livingEntity) {
		pkaPlayer.setNoDamageTicksGiven(10 / values[0]);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {

			@Override
			public void run() {
				pkaPlayer.setNoDamageTicksGiven(5);
			}
			
		}, 20l * values[1]);
	}

	@Override
	public void initialize(PKAPlayer pkaPlayer, int[] values, int rarity) {
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
