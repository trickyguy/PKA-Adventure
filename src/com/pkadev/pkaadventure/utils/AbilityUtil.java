package com.pkadev.pkaadventure.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.AbilityWithTimers;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.MessageType;

public class AbilityUtil {

	public static void clearPlayersAbilities(PKAPlayer pkaPlayer) {
		for (Integer i : pkaPlayer.getAbilities().keySet()) {
			stopTimers(pkaPlayer.getAbility(i));
		}
	}
	
	private static BukkitScheduler scheduler = Bukkit.getScheduler();
	private static void stopTimers(Ability ability) {
		AbilityWithTimers abilityWithTimers = null;
		if (ability instanceof AbilityWithTimers)
			abilityWithTimers = (AbilityWithTimers) ability;
		int[] tasks = abilityWithTimers.getTimers();
		for (int i = 0; i < abilityWithTimers.getTimerAmount(); i++) {
			if (tasks[i] != 0)	
				if (scheduler.isCurrentlyRunning(tasks[i]))
					scheduler.cancelTask(tasks[i]);
		}
	}
	
	public static boolean hasCooldown(Ability ability) {
		if (ability.getCoolDownCurrentTime() == 0l)
			return setCoolDown(ability);
		long timepassed = (System.currentTimeMillis() - (ability.getCoolDownCurrentTime() / 1000l));
		if(timepassed > 15l)
			return setCoolDown(ability);
		MessageUtil.sendMessage(ability.getPKAPlayer().getPlayer(), "You cannot use §6" + ability.getName() + " §fyet. §5[" + (15l - timepassed) + "s]", MessageType.SINGLE);
		return false;
	}
	
	private static boolean setCoolDown(Ability ability) {
		ability.setCooldownCurrentTime(System.currentTimeMillis());
		return false;
	}
	
	public static void notifyActivation(Ability ability) {
		MessageUtil.sendMessage(ability.getPKAPlayer().getPlayer(), "You activated " + ability.getName() + ".", MessageType.SINGLE);
	}
	
	public static void notifyDeactivation(Ability ability) {
		MessageUtil.sendMessage(ability.getPKAPlayer().getPlayer(), "Your " + ability.getName() + " ran out.", MessageType.SINGLE);
	}
	
	
}
