package com.pkadev.pkaadventure.utils;

import com.pkadev.pkaadventure.objects.BrokenOreBlock;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.particles.ParticleEffect;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.threads.OreTimer;
import com.pkadev.pkaadventure.types.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SkillsUtil {

	// TODO When specifying level and exp, need to make a check for which skill
	// information should be used.

	public static void load() {
		setSkillIds();
		setMiningValues();
	}

	public static void unload() {
		MessageUtil.log("Regenerated all broken ore blocks.");
		for(BrokenOreBlock block : BrokenOreBlock.getAllBlocks()) {
			block.getOreBlock().setType(block.getMaterial());
		}
	}
	
	public static void updateSkillItemWithStats(Player player, ItemStack itemStack, int level, int exp) {
		if (isSkillItem(itemStack)) {
			ItemType itemType = ElementsUtil.getItemTypeElement("skill");
			ItemMeta itemMeta = itemStack.getItemMeta();

			List<String> lore = itemMeta.getLore();
			List<String> newLore = new ArrayList<String>();

			for (int i = 0; i < lore.size(); i++) {
				String line = (String) lore.get(i);
				String editedLine = ChatColor.stripColor((String) lore.get(i));
				if (editedLine.startsWith("Level")) {
					String replacement = ElementsUtil.getLoreElementMod(itemType.getElements().get(i));
					line = replacement + level;
				} if (editedLine.startsWith("EXP")) {
					String replacement = ElementsUtil.getLoreElementMod(itemType.getElements().get(i));
					line = replacement + exp + "/" + getMaxExpFromLevel(level);
				}
				newLore.add(line);
			}

			short dura = (short) (itemStack.getType().getMaxDurability() - itemStack.getDurability());
			
			Material newMaterail = getSkillMaterial(itemStack, level, getMaterialSuffix(itemStack.getType()));
			itemStack.setType(newMaterail);
			
			short newDura = (short) (newMaterail.getMaxDurability() - dura);
			if(newDura < 0)
				itemStack.setDurability((short) 0);
			else
				itemStack.setDurability(newDura);

			itemMeta.setLore(newLore);
			itemMeta.setDisplayName(getSkillName(itemStack.getType()));
			itemStack.setItemMeta(itemMeta);
		}
	}

	public static List<Integer> getSkillMultipliers(ItemStack itemStack) {
		ItemType itemType = ElementsUtil.getItemTypeElement("skill");

		List<String> lore = itemStack.getItemMeta().getLore();
		List<Integer> enchants = new ArrayList<Integer>();

		for (int a = 0; a < itemType.getEndElements().size(); a++)
			enchants.add(Integer.valueOf(0));

		if (lore.size() <= 2)
			return enchants;

		for (int n = 0; n < itemType.getEndElements().size(); n++) {
			for (int y = 3; y < lore.size(); y++) {
				if (lore.get(y).startsWith(ElementsUtil.getLoreElementMod(itemType.getEndElements().get(n)))) {
					String string = ChatColor.stripColor(lore.get(y));
					String number = Character.toString(string.charAt(string.length() - 2));

					enchants.set(n, Integer.valueOf(Integer.parseInt(number)));
					break;
				}
			}
		}
		return enchants;
	}

	public static void setNewEnchantment(Player player, ItemStack itemStack) {
		ItemType itemType = ElementsUtil.getItemTypeElement("skill");
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);

		List<String> lore = itemStack.getItemMeta().getLore();
		List<String> endElements = itemType.getEndElements();
		List<Integer> enchants = getSkillMultipliers(itemStack);

		int idx = new Random().nextInt(enchants.size());
		int random = ((Integer) enchants.get(idx)).intValue();
		int[] randElements = getSkillEndElements(pkaPlayer);

		if (random == 0) {
			String prefix = ElementsUtil.getLoreElementMod(endElements.get(idx));
			String enchant = prefix + "+" + randElements[random] + "%";

			if (lore.size() <= 3) {
				lore.add("");
				lore.add(enchant);
			} else
				lore.add(enchant);

			MessageUtil.sendMessage(player, "§cYou have recieved the enchantment " + enchant, MessageType.SINGLE);
		} else {
			String existingLore = lore.get(3 + idx);

			MessageUtil.sendMessage(player, "§cYour existing enchantment " + existingLore + " was increased!", MessageType.SINGLE);

			int charNum = existingLore.length() - 2;
			String number = Character.toString(existingLore.charAt(charNum));

			int i = Integer.parseInt(number);
			if(i > MathUtil.getDouble(endElements.get(idx) + "_range"))
				return;

			String newLore = existingLore.replaceFirst(i + "%", i + 1 + "%");
			lore.set(3 + idx, newLore);
		}

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
	}

	public static int[] getSkillEndElements(PKAPlayer pkaPlayer) {
		List<String> endElements = FileUtil.getStringListFromConfig(FileUtil.getItemTypeConfig(), "skill.endelements", "itemtypes.yml");

		String[] array = endElements.toArray(new String[endElements.size()]);
		int[] attributes = MathUtil.getArray(pkaPlayer.getMiningLevel(), array);

		return attributes;
	}

	public static boolean subtractDurablity(Player player, ItemStack item) {
		int durability = item.getDurability();

		@SuppressWarnings("deprecation")
		Material material = Material.getMaterial(item.getTypeId());
		int maxDura = material.getMaxDurability();
		int dura = maxDura - durability;
		float percent = (dura * 100) / maxDura;

		if(durability == maxDura - 1) {
			MessageUtil.sendMessage(player, "§cYour " + ChatColor.stripColor(getSkillName(material)) + " has broken!", MessageType.SINGLE);
			player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			player.setItemInHand(null);
			return true;
		} if(percent <= 5)
			MessageUtil.sendMessage(player, "§cWARNING: Your " + ChatColor.stripColor(getSkillName(material)) + " is at " + percent + "% durability.", MessageType.SINGLE);

		item.setDurability((short) (durability + 1));
		return false;
	}

	public static void activateGoldFind(Player player, PKAPlayer pkaPlayer) {
		int currentGold = pkaPlayer.getGoldAmount();
		int gold = new Random().nextInt(4) + 1; //TODO Make this better
		pkaPlayer.setGoldAmount(currentGold + gold);
		
		//TODO getPlayerFromPKAPlayer();
		MessageUtil.sendMessage(player, "§6You received §l" + "+§6" + gold + "G from your Gold Find enchantment.", MessageType.SINGLE);
	}
	
	public static void giveBrokenOre(Player player, List<Integer> multipliers, Material material) {
		String oreName = getOreMaterialName(material);
		ItemType itemType = ElementsUtil.getItemTypeElement(material.toString().toLowerCase());
		
		int doubleDrop = multipliers.get(0);
		int multiplier = 1;
		
		if(checkRandomChance(doubleDrop, 0)) {
			multiplier = 2;
			MessageUtil.sendMessage(player, "§6You received one extra " + ChatColor.stripColor(oreName)
					+ " from your Double Drop enchantment.", MessageType.SINGLE);
		}
		
		ItemStack ore = new ItemStack(material, 1 * multiplier);
		ItemMeta oreMeta = ore.getItemMeta();
		oreMeta.setDisplayName(oreName);
		ArrayList<String> oreLore = new ArrayList<String>();
		oreLore.add(ElementsUtil.getLoreElementMod(itemType.getElements().get(0)));
		oreMeta.setLore(oreLore);
		ore.setItemMeta(oreMeta);
		player.getInventory().addItem(ore);
	}
	
	// Not needed, keeping it incase.
	/* public static boolean isAllZero(List<Integer> enchants) {
		for (int i = 1; i < enchants.size(); i++) {
			if ((enchants.get(i)) != 0) {
				return false;
			}
		}
		return true;
	}

	public static void setSkillElements(String reference, YamlConfiguration itemTypeConfig, String configFileReference) {
		List<Integer> items = FileUtil.getIntListFromConfig(itemTypeConfig, reference + ".id", configFileReference);
		for(Integer i : items) {
			if(i == 270) {
				int[] ids = new int[] {270, 274, 257, 278};
				for(int id : ids)
					createItemElement(id, reference);
			} if(i == 271) {
				int[] ids = new int[] {271, 275, 256, 279};
				for(int id : ids)
					createItemElement(id, reference);
			}
		}
	}

	public static void createItemElement (int id, String reference) {
		@SuppressWarnings("deprecation")
		ItemStack element = new ItemStack(id);
		ElementsUtil.setItemElement(reference, element);
	} */

	public static int getMaxExpFromLevel(int level) {
		int maxExp = (level + 3) * 8 * 4 * level + 128;
		return maxExp;
	}

	public static boolean isSkillItem(ItemStack itemStack) {
		Material type = itemStack.getType();
		if ((type.equals(Material.WOOD_PICKAXE)) || (type.equals(Material.STONE_PICKAXE)) || (type.equals(Material.IRON_PICKAXE)) || 
				(type.equals(Material.DIAMOND_PICKAXE)) || (type.equals(Material.WOOD_AXE)) || (type.equals(Material.STONE_AXE)) || 
				(type.equals(Material.IRON_AXE)) || (type.equals(Material.DIAMOND_AXE))) {
			return true;
		}
		return false;
	}

	public static String getMaterialSuffix(Material material) {
		if (material.toString().endsWith("_PICKAXE"))
			return "_PICKAXE";
		else if (material.toString().endsWith("_AXE"))
			return "_AXE";
		return null;
	}

	// TODO Improve this shit, make it more dynamic. Works for now though.

	public static Material getSkillMaterial(ItemStack itemStack, int level,
			String suffix) {
		if (level >= 0 && level < 25) {
			return Material.getMaterial("WOOD" + suffix);
		} else if (level >= 25 && level < 50) {
			return Material.getMaterial("STONE" + suffix);
		} else if (level >= 50 && level < 75) {
			return Material.getMaterial("IRON" + suffix);
		} else if (level >= 75 && level < 100) {
			return Material.getMaterial("DIAMOND" + suffix);
		} else if (level == 100) {
			return Material.getMaterial("DIAMOND" + suffix);
		}
		return null;
	}

	public static String getSkillName(Material material) {
		String materialName = material.toString();
		if (materialName.endsWith("_PICKAXE")) {
			switch (material) {
			default:
				return null;
			case WOOD_PICKAXE:
				return "§eBeginner Pickaxe";
			case STONE_PICKAXE:
				return "§aAmateur Pickaxe";
			case IRON_PICKAXE:
				return "§dElite Pickaxe";
			case DIAMOND_PICKAXE:
				return "§bMaster Pickaxe";
			}
		} if (materialName.endsWith("_AXE")) {
			switch (material) {
			default:
				return null;
			case WOOD_AXE:
				return "§eBeginner Axe";
			case STONE_AXE:
				return "§aAmateur Axe";
			case IRON_AXE:
				return "§dElite Axe";
			case DIAMOND_AXE:
				return "§bMaster Axe";
			}
		}

		return "Error";
	}

	public static String getOreMaterialName(Material material) {
		switch (material) {
		default:
			return null;
		case COAL_ORE:
			return "§fCoal Ore";
		case LAPIS_ORE:
			return "§fLapis Ore";
		case IRON_ORE:
			return "§fIron Ore";
		case GOLD_ORE:
			return "§fGold Ore";
		case DIAMOND_ORE:
			return "§fDiamond Ore";
		case EMERALD_ORE:
			return "§fEmerald Ore";
		}
	}

	public static void createBrokenOre(Block block, Material material, int time) {
		new BrokenOreBlock(block, material, time);
		block.setType(Material.STONE);

		if (!OreTimer.isRunning())
			OreTimer.start();
	}

	public static void removeBrokenOreBlock(BrokenOreBlock oreBlock) {
		Block block = oreBlock.getOreBlock();
		block.setType(oreBlock.getMaterial());

		BrokenOreBlock.getAllBlocks().remove(oreBlock);

		if (BrokenOreBlock.getAllBlocks().size() == 0)
			OreTimer.stop();
	}

	// TODO fix this hardcoded shit.

	public static Map<String, Integer> ore_values = new HashMap<>();
	public static Map<String, Integer> pickaxe_values = new HashMap<>();
	public static List<Integer> skill_ids = new ArrayList<Integer>();

	public static void setMiningValues() {
		ore_values.put("COAL_ORE", 0);
		ore_values.put("LAPIS_ORE", 1);
		ore_values.put("IRON_ORE", 2);
		ore_values.put("GOLD_ORE", 3);
		ore_values.put("DIAMOND_ORE", 4);
		ore_values.put("EMERALD_ORE", 5);

		pickaxe_values.put("WOOD_PICKAXE", 1);
		pickaxe_values.put("STONE_PICKAXE", 2);
		pickaxe_values.put("IRON_PICKAXE", 4);
		pickaxe_values.put("DIAMOND_PICKAXE", 6);	
	}

	public static void setSkillIds() {
		int[] ids = new int[] {270, 271, 274, 275, 278, 279, 257, 258};
		for(int id : ids)
			skill_ids.add(id);
	}

	public static boolean checkRandomChance(double original, double plus) {
		Random ran = new Random();
		double multiplier = 1;

		if (multiplier >= 1 || multiplier <= 99) {
			double modifier = 101 - (100 / multiplier);
			double d = modifier + ran.nextDouble() * (100 - modifier);

			if (d >= (100 - (original + plus)))
				return true;
			return false;
		} else {
			return false;
		}
	}

	public static double getAdditionalChance(int level) {
		double newLevel = 100 - level;
		double chance = newLevel / 10;
		return chance;
	}
	
	public static double defaultOreChance(Material material, double level) {
		switch (material) {
		default:
			return 0;
		case COAL_ORE:
			return (level * Math.PI) * 0.75;
		case LAPIS_ORE:
			return (level * Math.PI) * 0.7;
		case IRON_ORE:
			return (level * Math.PI) * 0.65;
		case GOLD_ORE:
			return (level * Math.PI) * 0.6;
		case DIAMOND_ORE:
			return (level * Math.PI) * 0.55;
		case EMERALD_ORE:
			return (level * Math.PI) * 0.5;
		}
	}

	public static int defaultOreExp(Material material) {
		switch (material) {
		default:
			return 0;
		case COAL_ORE:
			return getRandom(90, 110);
		case LAPIS_ORE:
			return getRandom(200, 240);
		case IRON_ORE:
			return getRandom(310, 370);
		case GOLD_ORE:
			return getRandom(420, 500);
		case DIAMOND_ORE:
			return getRandom(530, 630);
		case EMERALD_ORE:
			return getRandom(640, 760);
		}
	}

	public static int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}

	public static int getBlockCooldown(Material material) {
		switch (material) {
		default:
			return 0;
		case COAL_ORE:
			return 10;
		case LAPIS_ORE:
			return 11;
		case IRON_ORE:
			return 12;
		case GOLD_ORE:
			return 13;
		case DIAMOND_ORE:
			return 15;
		case EMERALD_ORE:
			return 16;
		}
	}

	public static boolean isUpgradable(int level) {
		switch (level) {
		default:
			return false;
		case 25:
			return true;
		case 50:
			return true;
		case 75:
			return true;
		case 100:
			return true;
		}
	}

	public static Material getItemUpgrade(Material material) {
		String materialName = material.toString();
		if (materialName.endsWith("_PICKAXE")) {
			switch (material) {
			default:
				return null;
			case WOOD_PICKAXE:
				return Material.STONE_PICKAXE;
			case STONE_PICKAXE:
				return Material.IRON_PICKAXE;
			case IRON_PICKAXE:
				return Material.DIAMOND_PICKAXE;
			}
		} if (materialName.endsWith("_AXE")) {
			switch (material) {
			default:
				return null;
			case WOOD_AXE:
				return Material.STONE_AXE;
			case STONE_AXE:
				return Material.IRON_AXE;
			case IRON_AXE:
				return Material.DIAMOND_AXE;
			}
		}
		return null;
	}

	public static void upgradeSkillItem(Player player, ItemStack item, Material material, String name) {
		short dura = (short) (item.getType().getMaxDurability() - item.getDurability());
		short newDura = (short) (material.getMaxDurability() - dura);
		item.setDurability(newDura);
		
		item.setType(material);
		item.getItemMeta().setDisplayName(name);

		MessageUtil.sendMessage(player, "§dYour pickaxe was upgraded to §l"
				+ ChatColor.stripColor(name) + ".", MessageType.SINGLE);
		SkillsUtil.createFirework(player, Color.FUCHSIA, Color.PURPLE);

		player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
		ParticleEffect.WITCH_MAGIC.display(player.getLocation(), 0, 1, 0, 14, 32);
	}

	public static void createFirework(Player player, Color color, Color fade) {
		Firework fireWork = (Firework) player.getWorld().spawnEntity(
				player.getLocation(), EntityType.FIREWORK);
		FireworkMeta fireWorkMeta = fireWork.getFireworkMeta();
		Random r = new Random();
		FireworkEffect.Type type = FireworkEffect.Type.BURST;

		FireworkEffect effect = FireworkEffect.builder()
				.flicker(r.nextBoolean()).withColor(color).withFade(fade)
				.with(type).trail(r.nextBoolean()).build();
		fireWorkMeta.addEffect(effect);
		fireWorkMeta.setPower(0);
		fireWork.setFireworkMeta(fireWorkMeta);
	}
}

