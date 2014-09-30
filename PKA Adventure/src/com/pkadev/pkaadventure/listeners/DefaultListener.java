package com.pkadev.pkaadventure.listeners;

import java.util.List;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.ProtocolInjector;
import org.spigotmc.ProtocolInjector.PacketTitle;
import org.spigotmc.ProtocolInjector.PacketTitle.Action;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.MobProcessor;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class DefaultListener implements Listener {

	private static DefaultListener i;
	private DefaultListener(){}
	
	public static DefaultListener i() {
		if (i == null) i = new DefaultListener(); return i;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerProcessor.loadPlayer(player);

		MessageUtil.printHoverable(player.getName(), "§f§lHOVER OVER ME", "", "§dThis is an example of hoverable text.");

		if(((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

			IChatBaseComponent header = ChatSerializer.a("{\"text\": \"§eWoody§6Craft §f- PKA Adventure\"}");
			IChatBaseComponent footer = ChatSerializer.a("{\"text\": \"Donate for premium features\"}");

			IChatBaseComponent title = ChatSerializer.a("{\"text\": \"§eWoody§6Craft\"}");
			IChatBaseComponent subTitle = ChatSerializer.a("{\"text\": \"§fWelcome to PKA Adventure\"}");

			connection.sendPacket(new ProtocolInjector.PacketTabHeader(header, footer));
			connection.sendPacket(new ProtocolInjector.PacketTitle(Action.TITLE, title));
			connection.sendPacket(new ProtocolInjector.PacketTitle(Action.SUBTITLE, subTitle));

			PacketTitle length = new PacketTitle(Action.TIMES, 10, 120, 10);
			connection.sendPacket(length);
		}
	}

	// Broken if the string contains a %
	public static String formatListIntoString(String name, List<String> list) {
		String d = name + "\n";
		int i = 0;

		for(String string : list) {
			if(!(i == list.size()))
				d += string + "\n";
			else
				d += string;
		} return d;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerProcessor.unloadPlayer(player);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.DEFAULT
				&& event.getSpawnReason() != SpawnReason.CUSTOM)
			event.setCancelled(true);
	}

	/* @EventHandler
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
	} */

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
