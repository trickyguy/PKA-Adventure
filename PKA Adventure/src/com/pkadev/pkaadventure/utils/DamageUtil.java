package com.pkadev.pkaadventure.utils;

import java.util.Random;

public class DamageUtil {
	private static Random random = new Random();
	
	public static double getFinalizedDamage(double damage, int[] attributesAttacker, int[] attributesDefender) {
		int attackerCritChance = 0;
		if (attributesAttacker.length == 5) {
			attackerCritChance = 100 / attributesAttacker[4];
		}
		double attackerStrength = 100d / (double) attributesAttacker[0];
		double defenderToughness = 100d / (double) attributesDefender[1];
		if (random.nextInt(100) < attackerCritChance) {
			damage = damage * 2d;
		}
		return (damage * attackerStrength) - (damage * defenderToughness);
	}
	
	public static double getFinalizedDamage(double minecraftDamage, double maxHealth) {
		if (minecraftDamage < 5)
			return 0;
		return (minecraftDamage / 20d) * maxHealth;	
	}
	
}
