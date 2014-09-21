package com.pkadev.pkaadventure.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.objects.PKALivingEntity;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.MobProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.utils.DamageUtil;

public class CombatListener implements Listener {

	private static CombatListener i; private CombatListener(){} public static CombatListener i() {if (i == null)i = new CombatListener();return i;}
	
	@SuppressWarnings("deprecation")
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
		
		PKALivingEntity pkaDamagee = null;
		PKALivingEntity pkaDamager = null;
		
		String damagerName = ""; //will remain empty if its a mob
		
		if (PlayerProcessor.isPlayer(damagee)) {
			pkaDamagee = PlayerProcessor.getPKAPlayer((Player) damagee);
			if (pkaDamagee == null) {
				event.setCancelled(true);
				return;
			}
			isDamageePlayer = true;
			for (ItemStack armorPiece : damagee.getEquipment().getArmorContents())
				armorPiece.setDurability((short) 0);
		}
		else if (MobProcessor.isMobMonster(damagee)) {
			PKAMob pkaMob = MobProcessor.getMobMonster(damagee).getPKAMob();
			if (pkaMob.getMobStance() == MobStance.GOOD || pkaMob.getMobStance() == MobStance.NPC) {
				event.setCancelled(true);
				return;
			}
			pkaDamagee = pkaMob;
		} else {
			event.setCancelled(true);
			damagee.remove();
			return;
		}
		
		if (damager == null)
			damager = projectile.getShooter();
		
		if (PlayerProcessor.isPlayer(damager)) {
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer((Player) damager);
			pkaDamager = pkaPlayer;
			if (pkaDamager == null) {
				event.setCancelled(true);
				return;
			}
			isDamagerPlayer = true;
			damagerName = pkaDamager.getName();
			if (pkaPlayer.getWeaponSlot() < 9)
				((Player) damager).getInventory().getItem(pkaPlayer.getWeaponSlot()).setDurability((short) 0);
		}
		else if (MobProcessor.isMobMonster(damager)) {
			pkaDamager = MobProcessor.getMobMonster(damager).getPKAMob();
		} else {
			event.setCancelled(true);
			damager.remove();
			if (projectile != null)
				projectile.remove();
			return;
		}
		
		if (isDamagerPlayer && isDamageePlayer) {
			event.setCancelled(true);
			return;
		}
		
		if (damagee.getNoDamageTicks() > getNoDamageTicks(damagee, damager, isDamageePlayer, isDamagerPlayer)) {
			event.setCancelled(true);
			return;
		}
		
		double finalizedDamage = DamageUtil.getFinalizedDamage(pkaDamager.getDamage(), pkaDamager.getAttributes(), pkaDamagee.getAttributes());
		
		if (pkaDamagee.damage(finalizedDamage, damagerName)) {
			if (isDamageePlayer) {
				PlayerProcessor.damagePlayerLethal(PlayerProcessor.getPlayer(damagee));
				event.setCancelled(true);
			} else {
				event.setDamage(100d);
			}
		} else {
			event.setDamage(0d);
		}
		
		if (isDamageePlayer)
			PlayerProcessor.updateHealth((Player) damagee);
		else {
			MobProcessor.updateHealth(damagee, pkaDamagee); //only changes name
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

	@EventHandler
	public void onEntityDamageByEnvironment(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE)
			return;
		
		LivingEntity livingEntity = (LivingEntity) event.getEntity();
		PKALivingEntity pkaLivingEntity = null;
		boolean isDamageePlayer = false;
		if (PlayerProcessor.isPlayer(livingEntity)) {
			isDamageePlayer = true;
			pkaLivingEntity = PlayerProcessor.getPKAPlayer(PlayerProcessor.getPlayer(livingEntity));
			if (pkaLivingEntity == null)
				return;
		} else if (MobProcessor.isMobMonster(livingEntity)) {
			pkaLivingEntity = MobProcessor.getMobMonster(livingEntity).getPKAMob();
		} else {
			event.setCancelled(true);
			livingEntity.remove();
		}

		double finalizedDamage = DamageUtil.getFinalizedDamage(event.getDamage(), pkaLivingEntity.getMaxHealth());
		
		if (finalizedDamage == 0d) {
			event.setCancelled(true);
			return;
		}
		
		if (pkaLivingEntity.damage(finalizedDamage, "")) {
			if (isDamageePlayer) {
				PlayerProcessor.damagePlayerLethal(PlayerProcessor.getPlayer(livingEntity));
				event.setCancelled(true);
			} else {
				event.setDamage(100d);
			}
		} else {
			event.setDamage(0d);
		}
		
		if (!isDamageePlayer)
			MobProcessor.updateHealth(livingEntity, pkaLivingEntity); //only changes name
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
	
	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		if (!(event.getEntityType() == EntityType.PLAYER))
			return;
		event.setCancelled(true);
	}
	
}
