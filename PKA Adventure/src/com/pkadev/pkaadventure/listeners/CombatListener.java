package com.pkadev.pkaadventure.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.MobProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.utils.DamageUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class CombatListener implements Listener {

	private static CombatListener i; private CombatListener(){} public static CombatListener i() {if (i == null)i = new CombatListener();return i;}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		LivingEntity damagee = 				null;
		LivingEntity damager = 				null;
		Projectile projectile = 			null;
		
		if (event.getDamager() instanceof LivingEntity) {
			damager = 						(LivingEntity) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			projectile = 					(Projectile) event.getDamager();
		} else {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity() instanceof LivingEntity) {
			damagee = 						(LivingEntity) event.getEntity();
		} else {
			event.setCancelled(true);
			return;
		}
		
		boolean isDamageePlayer = false; //if false, its a mob
		boolean isDamagerPlayer = false; //if false, its a mob
		
		PKAPlayer damageePlayer = null;
		PKAPlayer damagerPlayer = null;
		MobMonster damageeMob = null;
		MobMonster damagerMob = null;
		
		if (PlayerProcessor.isPlayer(damagee)) {
			damageePlayer = PlayerProcessor.getPKAPlayer((Player) damagee);
			if (damageePlayer == null) {
				event.setCancelled(true);
				return;
			}
			isDamageePlayer = true;
		}
		else if (MobProcessor.isMobMonster(damagee)) {
			damageeMob = MobProcessor.getMobMonster(damagee);
		} else {
			event.setCancelled(true);
			damagee.remove();
			return;
		}
		
		if (damager == null)
			damager = projectile.getShooter();
		
		if (PlayerProcessor.isPlayer(damager)) {
			damagerPlayer = PlayerProcessor.getPKAPlayer((Player) damager);
			if (damagerPlayer == null) {
				event.setCancelled(true);
				return;
			}
			isDamagerPlayer = true;
		}
		else if (MobProcessor.isMobMonster(damager)) {
			damagerMob = MobProcessor.getMobMonster(damager);
		} else {
			event.setCancelled(true);
			damager.remove();
			if (projectile != null)
				projectile.remove();
			return;
		}
		
		if (damagee.getNoDamageTicks() < getNoDamageTicks(damagee, damager, isDamageePlayer, isDamagerPlayer)) {
			event.setCancelled(true);
			return;
		}
		
		if (isDamagerPlayer) {
			if (isDamageePlayer)
				event.setCancelled(true);
			else {
				if (!damageMob(damageeMob, damagerPlayer, (Player) damager))
					event.setCancelled(true);
			}
		} else {
			if (isDamageePlayer)
				if (!damagePlayer((Player) damagee, damageePlayer, damagerMob))
					event.setCancelled(true);
			else {
				damageMob(damageeMob, damagerPlayer, (Player) damager);
			}
		}
	}
	
	private int getNoDamageTicks(LivingEntity damagee, LivingEntity damager, boolean isDamageePlayer, boolean isDamagerPlayer) {
		int damageeNoDamageTicks = -1;
		int damagerNoDamageTicks = -1;
		
		if (isDamageePlayer)
			damageeNoDamageTicks = PlayerProcessor.getPKAPlayer((Player) damagee).getNoDamageTicksTaken();
		if (isDamagerPlayer)
			damagerNoDamageTicks = PlayerProcessor.getPKAPlayer((Player) damager).getNoDamageTicksGiven();
		
		if (damageeNoDamageTicks == -1 && damagerNoDamageTicks == -1)
			return 10;
		else if (damageeNoDamageTicks == -1)
			return damagerNoDamageTicks;
		else if (damagerNoDamageTicks == -1)
			return damageeNoDamageTicks;
		else {
			return damagerNoDamageTicks;
		}
	}
	
	private boolean damageMob(MobMonster mobMonster, PKAPlayer pkaPlayer, Player player) {
		double damage = pkaPlayer.getDamage();
		int[] attributesAttacker = pkaPlayer.getAttributes();
		int[] attributesDefender = mobMonster.getPKAMob().getAttributes();
		
		damageMob(mobMonster, DamageUtil.getFinalizedDamage(damage, attributesAttacker, attributesDefender), player.getName());
		return true;
	}
	
	private void damageMob(MobMonster mobMonsterDamagee, MobMonster mobMonsterDamager) {
		double damage = mobMonsterDamager.getPKAMob().getDamage();
		int[] attributesAttacker = mobMonsterDamager.getPKAMob().getAttributes();
		int[] attributesDefender = mobMonsterDamagee.getPKAMob().getAttributes();
		
		damageMob(mobMonsterDamagee, DamageUtil.getFinalizedDamage(damage, attributesAttacker, attributesDefender), "");
	}
	
	private void damageMob(MobMonster mobMonster, double finalizedDamage, String damagerName) {
		MobProcessor.damageMobByEntity((LivingEntity)mobMonster.getEntity(), finalizedDamage, damagerName);
	}
	
	private boolean damagePlayer(Player player, PKAPlayer pkaPlayer, MobMonster mobMonster) {
		double damage = mobMonster.getPKAMob().getDamage();
		int[] attributesAttacker = mobMonster.getPKAMob().getAttributes();
		int[] attributesDefender = pkaPlayer.getAttributes();
		
		PlayerProcessor.damagePlayerByEntity(player, pkaPlayer, DamageUtil.getFinalizedDamage(damage, attributesAttacker, attributesDefender));
		return true;
	}

	@EventHandler
	public void onEntityDamageByEnvironment(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE)
			return;
		
		LivingEntity livingEntity = (LivingEntity) event.getEntity();

		if (livingEntity instanceof Player) {
			PlayerProcessor.damagePlayerByEnvironment((Player) event.getEntity(), event.getDamage());
		} else if (MobProcessor.isMobMonster(livingEntity)) {
			MobProcessor.damageMobByEnvironment(livingEntity, event.getDamage());
		} else {
			livingEntity.remove();
		}
		
		event.setDamage(0d);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		event.setDroppedExp(0);	//TODO do the exp drop sytem
		event.getDrops().clear();
		
		LivingEntity livingEntity = event.getEntity();
		
		if (MobProcessor.isMobMonster(livingEntity)) {
			MobProcessor.mobDeath(MobProcessor.getMobMonster(livingEntity), livingEntity.getLocation());
		}
	}
}
