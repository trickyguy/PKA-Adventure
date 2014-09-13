package com.pkadev.pkaadventure.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.MobProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class DefaultListener implements Listener {

	private static DefaultListener i; private DefaultListener(){} public static DefaultListener i() {if (i == null)i = new DefaultListener();return i;}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerProcessor.loadPlayer(event.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		PlayerProcessor.unloadPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.DEFAULT
				&& event.getSpawnReason() != SpawnReason.CUSTOM)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		event.setCancelled(true);
		MessageUtil.sendMessage(player, "You are not allowed to break blocks!", MessageType.SINGLE);
		MessageUtil.sendMessage(player, "TEMP: To be able to build type /pka leave", MessageType.SINGLE);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		event.setCancelled(true);
		MessageUtil.sendMessage(player, "You are not allowed to place blocks!", MessageType.SINGLE);
		MessageUtil.sendMessage(player, "TEMP: To be able to build type /pka leave", MessageType.SINGLE);
	}
	
	@EventHandler
	public void onMobInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof LivingEntity))
			return;
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(event.getPlayer());
		if (pkaPlayer == null)
			return;
		if (!MobProcessor.isMobMonster(event.getRightClicked())) {
			event.getRightClicked().remove();
			return;
		}
		PKAMob pkaMob = MobProcessor.getMobMonster(event.getRightClicked()).getPKAMob();
		if (pkaMob.getMobStance() == MobStance.NPC)
			InventoryUtil.openInventoryDelayed(event.getPlayer(), pkaMob.getLevel(), pkaMob.getName());
	}
	
}
