package com.pkadev.pkaadventure.listeners;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import com.pkadev.pkaadventure.utils.ItemUtil;

public class CombatListener implements Listener {

	private static CombatListener i; private CombatListener(){} public static CombatListener i() {if (i == null)i = new CombatListener();return i;}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			event.setCancelled(true);
			return;
		}

		double damage = 0d;
		int[] attributesAttacker = new int[]{0, 0, 0, 0};
		String damagerName = "";

		if (MobProcessor.isMobMonster(event.getEntity())) {
			LivingEntity livingEntity = (LivingEntity) event.getEntity();
			if (livingEntity.getNoDamageTicks() > 10) {
				event.setCancelled(true);
				return;
			}

			if (MobProcessor.isMobMonster(event.getDamager())) {
				PKAMob pkaMob = MobProcessor.getMobMonster(livingEntity).getPKAMob();
				damage = pkaMob.getDamage();
				attributesAttacker = pkaMob.getAttributes();
			} else if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getEntity();
				PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
				if (pkaPlayer.getWeaponSlot() != player.getInventory().getHeldItemSlot()) {
					event.setCancelled(true);
					return;
				}
				damage = pkaPlayer.getDamage();
				attributesAttacker = pkaPlayer.getAttributes();
				damagerName = player.getName();
			} else {
				event.getEntity().remove();
			}
			if (damage == 0d)
				return;

			MobProcessor.damagePlayerByEntity(livingEntity, damage, attributesAttacker, damagerName);
		} else if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (player.getNoDamageTicks() > 10) {
				event.setCancelled(true);
				return;
			}

			if (MobProcessor.isMobMonster(event.getDamager())) {
				PKAMob pkaMob = MobProcessor.getMobMonster(event.getDamager()).getPKAMob();
				damage = pkaMob.getDamage();
				attributesAttacker = pkaMob.getAttributes();
			} else if (event.getDamager() instanceof Player) {
				//TODO
				event.setCancelled(false);
				return;
			} else {
				event.getEntity().remove();
			}
			PlayerProcessor.damagePlayerByEntity(player, damage, attributesAttacker);
		} else {
			event.getEntity().remove();
		}


	}

	@EventHandler
	public void onEntityDamageByEnvironment(EntityDamageEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			event.setCancelled(true);
			return;
		}

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
			HashMap<String, ItemStack> drops = ItemUtil.getNewItemDrop(pkaMob.getDamageDoneBy().keySet(), pkaMob.getLevel(), pkaMob.getRareItemInt());
			for (String player : drops.keySet()) {
				if (player == "")
					continue;
				Item item = entity.getWorld().dropItem(entity.getLocation(), drops.get(player));
				ItemUtil.addDroppedItem(item, player);
			}
		} else if (entity instanceof Player) {
			PlayerProcessor.playerDeath(entity);
		}
	}
}
