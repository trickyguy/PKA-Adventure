package com.pkadev.pkaadventure.processors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.w3c.dom.Entity;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class CommandProcessor implements CommandExecutor {

	private static CommandProcessor i; private CommandProcessor(){} public static CommandProcessor i() {if (i == null)i = new CommandProcessor();return i;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl,
			String[] args) {
		int argsLength = args.length;

		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;

		if (argsLength == 0) {
			InventoryUtil.openInventory(player, "selection");
		} else if (argsLength == 1) {
			if (args[0].equalsIgnoreCase("create")) {
				MessageUtil.sendMessage(player, "/create fileName mobName radius level amount mob strength stance type", MessageType.SINGLE);
			} else if (args[0].equalsIgnoreCase("test")) {
				for (org.bukkit.entity.Entity entity : player.getLocation().getWorld().getEntities()) {
					if (entity instanceof LivingEntity && !(entity instanceof Player))
						if (entity.getLocation().distanceSquared(player.getLocation()) < 144)
							MessageUtil.d("" + ((LivingEntity) entity).getCustomName());
				}
			} else {
				invalidCommand(player);
			}
		} else if (argsLength == 3) {
			if (args[0].equalsIgnoreCase("create")) {
				//pka create lootcrate fileName
				if (args[1].equalsIgnoreCase("lootcrate")) {
					newSpawnNode(player, null, args[2]);
				} else {
					invalidCommand(player);
				}
			} else {
				invalidCommand(player);
			}
		} else if (argsLength == 4) {
			//pka create beacon name fileName
			if (args[1].equalsIgnoreCase("beacon")) {
				String name = args[2].replace('_', ' ');
				newSpawnNode(player, name, args[3]);
			} else {
				invalidCommand(player);
			}
		} else if (argsLength == 10) {
			newSpawnNode(player, args);
		} else {
			invalidCommand(player);
		}

		return true;
	}

	private static void invalidCommand(Player player) {
		MessageUtil.sendMessage(player, "Not a valid command", MessageType.SINGLE);
	}

	public void newSpawnNode(Player player, String name, String fileName) {
		if (name.equals(""))
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), null, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Lootcrate node", MessageType.SINGLE);
			}
		else {
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), new String[]{name}, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Beacon node", MessageType.SINGLE);
			}
		}
	}
	// 0      1      2       3 4 5  6      7       8      9
	//create testMob testMob 1 1 1 zombie minion hostile bad
	public void newSpawnNode(Player player, String[] args) {
		String[] newArgs = new String[8];
		int j = 0;
		for (int i = 2; i < 10; i++) {
			newArgs[j] = args[i];
			j++;
		}
		String fileName = args[1];
		if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), newArgs, fileName))
			MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
		else {
			MessageUtil.sendMessage(player, "created new Mob node", MessageType.SINGLE);
		}
	}

}
