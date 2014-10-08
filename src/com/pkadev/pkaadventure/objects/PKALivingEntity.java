package com.pkadev.pkaadventure.objects;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.types.AbilityTriggerType;

public class PKALivingEntity {

	private boolean isPlayer = false;
	private String name;
	private double damage;
	private int[] attributes = new int[]{0, 0, 0, 0};
	private double maxHealth;
	private double health;
	private int level;
	private HashMap<Integer, Ability> abilities = new HashMap<Integer, Ability>();
	private HashMap<String, Double> damageDoneBy = new HashMap<String, Double>();
	private double totalDamageReceived;
	
	/**
	 * player
	 * @param name
	 * @param damage
	 * @param health
	 */
	public PKALivingEntity(String name, int level, double damage, double maxHealth, double health) {
		isPlayer = true;
		this.level = level;
		this.name = name;
		this.damage = damage;
		this.maxHealth = maxHealth;
		this.health = health;
	}
	
	/**
	 * mob
	 * @param name
	 * @param attributes
	 * @param health
	 * @param abilities
	 */
	public PKALivingEntity(String name, double damage, int level, int[] attributes, double health, HashMap<Integer, Ability> abilities) {
		this.name = name;
		this.damage = damage;
		this.attributes = attributes;
		this.maxHealth = health;
		this.health = health;
		this.abilities = abilities;
		this.level = level;
		damageDoneBy.put("", 0d);
	}
	
	public boolean isPlayer() {
		return isPlayer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getDamage() {
		return damage;
	}
	public void setDamage(double damage) {
		this.damage = damage;
	}
	public boolean damage(double finalizedDamage, String damagerName) {
		double tempHealth = health - finalizedDamage;
		if (tempHealth <= 0d) {
			double damageDifference = finalizedDamage - (0d - tempHealth);
			addDamageDoneBy(damagerName, damageDifference);
			return true;
		} else {
			health = tempHealth;
			addDamageDoneBy(damagerName, finalizedDamage);
		}
		return false;
	}
	public int[] getAttributes() {
		return attributes;
	}
	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}
	public void addAttributes(int[] attributes) {
		for (int i = 0; i < 4; i++) {
			this.attributes[i] += attributes[i];
		}
	}
	public void removeAttributes(int[] attributes) {
		for (int i = 0; i < 4; i++) {
			this.attributes[i] -= attributes[i];
		}
	}
	public void clearAttributes() {
		for (int i = 0; i < 4; i++) {
			this.attributes[i] = 0;
		}
	}
	public double getMaxHealth() {
		return maxHealth;
	}
	public double getHealth() {
		return health;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public HashMap<Integer, Ability> getAbilities() {
		return abilities;
	}
	public Ability getAbility(Integer i) {
		return abilities.get(i);
	}
	public void setAbility(Integer i, Ability ability) {
		abilities.put(i, ability);
	}
	public void removeAbility(Integer i) {
		abilities.remove(i);
	}
	public void triggerAbility(Integer i, LivingEntity livingEntity, AbilityTriggerType abilityTriggerType) {
		Ability ability = abilities.get(i);
		if (ability == null)
			return;
		ability.trigger(livingEntity, abilityTriggerType);
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void addLevel() {
		level += 1;
	}
	public HashMap<String, Double> getDamageDoneBy() {
		return damageDoneBy;
	}
	public void setDamageDoneBy(HashMap<String, Double> damageDoneBy) {
		this.damageDoneBy = damageDoneBy;
	}
	/**
	 * gives you a list of all the players that have done damage, and gives them a value of 0-1 depending on what percent of the mob they killed
	 * INTERNAL: note, it does not return the damageDoneBy list, it makes sure to calculate the percentages
	 * @return
	 */
	public HashMap<String, Double> getRewardPercentages() {
		HashMap<String, Double> rewardPercents = new HashMap<String, Double>();
		for (String playerName : damageDoneBy.keySet()) {
			if (playerName == "")
				continue;
			double rewardPercent = damageDoneBy.get(playerName) / totalDamageReceived;
			if (rewardPercent < 0.1)
				rewardPercent = 0.1;
			else if(rewardPercent > 1)
				rewardPercent = 1;
			rewardPercents.put(playerName, Double.valueOf(rewardPercent));
		}
		return rewardPercents;
	}
	
	public void addDamageDoneBy(String playerName, double damage) {
		if (damageDoneBy.containsKey(playerName))
			damage = damageDoneBy.get(playerName) + damage;
		damageDoneBy.put(playerName, damage);
	}
}
