package com.pkadev.pkaadventure.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.MobProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.utils.DamageUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;

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
		
		if (damagee.getNoDamageTicks() > 10) {
			event.setCancelled(true);
			return;
		}	
		
		int[] attackerAttributes = 			null;
		int[] defenderAttributes = 			null;
		double damage = 					0d;
		boolean isDamageePlayer = 			false;
		String damagerName = 				"";
		PKAPlayer pkaPlayer = 				null;
		PKAMob pkaMob = 					null;
		
		if (projectile == null) {
			if (PlayerProcessor.isPlayer(damager)) {
				Player player = 			PlayerProcessor.getPlayer(damager);
				damagerName = 				player.getName();
				pkaPlayer = 				PlayerProcessor.getPKAPlayer(damagerName);
				if (pkaPlayer == null) {
					event.setCancelled(true);
					return;
				}
				attackerAttributes = 		pkaPlayer.getAttributes();
				damage = 					pkaPlayer.getDamage();
			} else if (MobProcessor.isMobMonster(damager)) {
				pkaMob = 			MobProcessor.getMobMonster(damager).getPKAMob();
				attackerAttributes = 		pkaMob.getAttributes();
				damage =					pkaMob.getDamage();
			} else {
				event.getDamager().remove();
				event.setCancelled(true);
				return;
			}
		} else if (projectile != null) {
			if (PlayerProcessor.isPlayer(projectile.getShooter())) {
				pkaPlayer = 				PlayerProcessor.getPKAPlayer(PlayerProcessor.getPlayer(projectile.getShooter()));
				if (pkaPlayer == null) {
					event.setCancelled(true);
					return;
				}
				attackerAttributes = 		pkaPlayer.getAttributes();
				damage =					pkaPlayer.getDamage();
			} else if (MobProcessor.isMobMonster(projectile.getShooter())) {
				pkaMob =					MobProcessor.getMobMonster(projectile.getShooter()).getPKAMob();
				attackerAttributes = 		pkaMob.getAttributes();
				damage =					pkaMob.getDamage();
			} else {
				event.setCancelled(true);
				damager.remove();
				projectile.getShooter().remove();
				return;
			}
		}
		
		if (PlayerProcessor.isPlayer(damagee)) {
			pkaPlayer =						PlayerProcessor.getPKAPlayer((Player) damagee);
			if (pkaPlayer == null) {
				event.setCancelled(true);
				return;
			}
			isDamageePlayer = 				true;
			defenderAttributes = 			pkaPlayer.getAttributes();
		} else if (MobProcessor.isMobMonster(damagee)) {
			pkaMob = 						MobProcessor.getMobMonster(damagee).getPKAMob();
			defenderAttributes = 			pkaMob.getAttributes();
		} else {
			event.setCancelled(true);
			damagee.remove();
			return;
		}
		
		double finalizedDamage = 			DamageUtil.getFinalizedDamage(damage, attackerAttributes, defenderAttributes);
		event.setDamage(0d);
		
		if (isDamageePlayer) {
			PlayerProcessor.damagePlayerByEntity((Player) damagee, pkaPlayer, finalizedDamage);
		} else {
			MobProcessor.damageMobByEntity(damagee, finalizedDamage, damagerName);
		}
	}

	@EventHandler
	public void onEntityDamageByEnvironment(EntityDamageEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			event.setCancelled(true);
			return;
		}

		event.setDamage(0d);
		LivingEntity livingEntity = (LivingEntity) event.getEntity();

		if (livingEntity instanceof Player) {
			PlayerProcessor.damagePlayerByEnvironment((Player) event.getEntity(), event.getDamage());
		} else if (MobProcessor.isMobMonster(livingEntity)) {
			MobProcessor.damageMobByEnvironment((LivingEntity) event.getEntity(), event.getDamage());
		} else {
			livingEntity.remove();
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		event.setDroppedExp(0);//TODO do the exp drop sytem
		event.getDrops().clear();

		Entity entity = event.getEntity();
		
		if (MobProcessor.isMobMonster(entity)) {
			MobMonster mobMonster = MobProcessor.getMobMonster(entity);
			MobProcessor.mobDeath(mobMonster);
			PKAMob pkaMob = mobMonster.getPKAMob();
			pkaMob.getDamageDoneBy().remove("");
			HashMap<String, List<ItemStack>> drops = ItemUtil.getNewItemDrop(pkaMob.getDamageDoneBy().keySet(), pkaMob.getMobName(), pkaMob.getLevel(), pkaMob.getRareItemInt());
			for (String player : drops.keySet()) {
				for (ItemStack itemStack : drops.get(player)) {
					Item item = entity.getWorld().dropItem(entity.getLocation(), itemStack);
					ItemUtil.addDroppedItem(item, player);
				}
			}
		} else if (entity instanceof Player) {
			((Player) entity).kickPlayer("Something went wrong. You are not supposed to die.");
		}
	}
}
