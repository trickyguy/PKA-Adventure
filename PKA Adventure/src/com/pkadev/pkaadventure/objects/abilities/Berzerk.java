package com.pkadev.pkaadventure.objects.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.AbilityWithTimers;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.AbilityTriggerType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.AbilityUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class Berzerk extends AbilityWithTimers implements Ability  {

	//values[0] = noDamageTicks multiplier 2 or 3
	//values[1] = seconds
	
	private Ability ability = this;
	private PKAPlayer pkaPlayer;
	private int[] values;
	private int rarity;
	private String reference = "ability_berzerk";
	private String name = "Berzerk";
	private int cooldownTime = 15;
	private long coolDownCurrentTime = 0l;
	
	@Override
	public void trigger(LivingEntity livingEntity, AbilityTriggerType abilityTriggerType) {
		if (abilityTriggerType != AbilityTriggerType.CLICK)
			return;
		if (AbilityUtil.hasCooldown(this))
			return;
		AbilityUtil.notifyActivation(this);
		pkaPlayer.setNoDamageTicksGiven(10 / values[0]);
		
		int i = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {

			@Override
			public void run() {
				pkaPlayer.setNoDamageTicksGiven(-1);
				AbilityUtil.notifyDeactivation(ability);
			}
			
		}, 20l * values[1]);
		this.addTimer(i, 0); //DONT FORGET THIS
	}

	@Override
	public void initialize(PKAPlayer pkaPlayer, int[] values, int rarity) {
		this.pkaPlayer = pkaPlayer;
		this.values = values;
		this.rarity = rarity;
		
		this.initialize(1); //DONT FORGET THIS
	}
	
	@Override
	public PKAPlayer getPKAPlayer() {
		return pkaPlayer;
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

	@Override
	public int getCooldownTime() {
		return cooldownTime;
	}
	
	@Override
	public long getCoolDownCurrentTime() {
		return coolDownCurrentTime;
	}
	
	@Override
	public void setCooldownCurrentTime(long coolDownCurrentTime) {
		this.coolDownCurrentTime = coolDownCurrentTime;
	}
	
}
