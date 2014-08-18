package com.pkadev.pkaadventure.utils;

import java.util.Random;

public class DamageUtil {
	private static Random random = new Random();
	
	public static double getFinalizedDamage(double damage, int[] attributesAttacker, int[] attributesDefender) {
		int attackerCritChance = 0;
		if (attributesAttacker.length == 5) {
			attackerCritChance = 100 / attributesAttacker[4];
		}
		double attackerStrength = (double) attributesAttacker[0] / 100d;
		double defenderToughness = (double) attributesDefender[1] / 100d;
		if (Double.isInfinite(attackerStrength))
			attackerStrength = 0d;
		if (Double.isInfinite(defenderToughness))
			defenderToughness = 0d;
		if (random.nextInt(100) < attackerCritChance) {
			damage = damage * 2d;
		}
		damage = damage + (damage * attackerStrength) - (damage * defenderToughness);
		return damage;
	}
	
	public static double getFinalizedDamage(double minecraftDamage, double maxHealth) {
		if (minecraftDamage < 5)
			return 0;
		return (minecraftDamage / 20d) * maxHealth;	
	}
	
}
