package com.pkadev.pkaadventure.interfaces;

import org.bukkit.entity.LivingEntity;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.AbilityTriggerType;

public interface Ability {

	/**
	 * @param player
	 * @param params: depends on the ability being triggered, and will affect that ability in some way
	 */
	public void trigger(LivingEntity livingEntity, AbilityTriggerType abilityTriggerType);
	public void initialize(PKAPlayer pkaPlayer, int[] values, int rarity);
	
	public String getReference();
	public int[] getValues();
	public int getRarity();
	public String getName();
	public int getCooldownTime();
	public long getCoolDownCurrentTime();
	public void setCooldownCurrentTime(long coolDownCurrentTime);
	public PKAPlayer getPKAPlayer();

}
