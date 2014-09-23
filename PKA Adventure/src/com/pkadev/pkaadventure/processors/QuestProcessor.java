package com.pkadev.pkaadventure.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAQuest;
import com.pkadev.pkaadventure.types.QuestState;
import com.pkadev.pkaadventure.utils.FileUtil;

public class QuestProcessor {

	private static Map<String, List<String>> npcQuests = null;
	private static Map<String, List<String>> mobQuests = null;
	private static ConfigurationSection quests = null;
	private static Random random = null;
	
	public static void load() {
		YamlConfiguration config = FileUtil.getQuestConfig();
		npcQuests = new HashMap<String, List<String>>();
		mobQuests = new HashMap<String, List<String>>();
		quests = config.getConfigurationSection("Quests");
		random = new Random();
		
		if (quests == null)
			return;
		Set<String> questNames = quests.getKeys(false);
		if (questNames == null)
			return;
		for (String questName : questNames) {
			ConfigurationSection quest = quests.getConfigurationSection(questName);
			if (quest == null)
				continue;
			
			ConfigurationSection npcs = quest.getConfigurationSection("npcs");
			if (npcs == null)
				continue;
			Set<String> npcNames = npcs.getKeys(false);
			if (npcNames == null)
				continue;
			
			for (String npcName : npcNames) {
				if (!npcQuests.containsKey(npcName)) {
					npcQuests.put(npcName, new ArrayList<String>());
				}
				if (!npcQuests.get(npcName).contains(questName)) {
					npcQuests.get(npcName).add(questName);
				}
			}
			
			ConfigurationSection mobs = quest.getConfigurationSection("mobs");
			if (mobs == null)
				continue;
			Set<String> mobNames = mobs.getKeys(false);
			if (mobNames == null)
				continue;
			
			for (String mobName : mobNames) {
				if (!mobQuests.containsKey(mobName)) {
					mobQuests.put(mobName, new ArrayList<String>());
				}
				if (!mobQuests.get(mobName).contains(questName)) {
					mobQuests.get(mobName).add(questName);
				}
			}
		}
	}
	
	private static PKAQuest getPKAQuestFromNPC(String npcName, String playerName) {
		if (npcQuests.containsKey(npcName)) {
			List<String> quests = npcQuests.get(npcName);
			for (int i = 0; i < quests.size(); i++) {
				String questName = quests.get(i);
				PKAQuest pkaQuest = getPKAQuestFromQuest(questName, playerName);
				if ((pkaQuest != null && !pkaQuest.getQuestState().equals(QuestState.COMPLETED)) || i == quests.size() - 1)
					return pkaQuest;
			}
		}
		return null;
	}
	
	private static PKAQuest getPKAQuestFromMob(String mobName, String playerName) {
		if (mobQuests.containsKey(mobName)) {
			List<String> quests = mobQuests.get(mobName);
			for (int i = 0; i < quests.size(); i++) {
				String questName = quests.get(i);
				PKAQuest pkaQuest = getPKAQuestFromQuest(questName, playerName);
				if ((pkaQuest != null && !pkaQuest.getQuestState().equals(QuestState.COMPLETED)) || i == quests.size() - 1)
					return pkaQuest;
			}
		}
		return null;
	}

	private static PKAQuest getPKAQuestFromQuest(String questName, String playerName) {
		PKAQuest pkaQuest = Main.instance.getDatabase().find(PKAQuest.class).where().ieq("playerName", playerName).eq("questName", questName).findUnique();
		if (pkaQuest == null) {
			pkaQuest = new PKAQuest(questName, playerName);
			Main.instance.getDatabase().save(pkaQuest);
			pkaQuest = Main.instance.getDatabase().find(PKAQuest.class).where().ieq("playerName", playerName).eq("questName", questName).findUnique();
		}
		return pkaQuest;
	}
}
