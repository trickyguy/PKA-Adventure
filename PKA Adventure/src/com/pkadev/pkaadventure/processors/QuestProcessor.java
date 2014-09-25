package com.pkadev.pkaadventure.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.objects.PKAMob;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.PKAQuest;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.QuestCompletionType;
import com.pkadev.pkaadventure.types.QuestState;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.LocationUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class QuestProcessor {

	private static Map<String, List<String>> npcQuests = null;
	private static Map<String, List<String>> mobQuests = null;
	private static ConfigurationSection quests = null;
	
	public static void load() {
		YamlConfiguration config = FileUtil.getQuestConfig();
		npcQuests = new HashMap<String, List<String>>();
		mobQuests = new HashMap<String, List<String>>();
		quests = config.getConfigurationSection("Quests");
		
		if (quests == null)
			return;
		Set<String> questReferences = quests.getKeys(false);
		if (questReferences == null)
			return;
		for (String questReference : questReferences) {
			ConfigurationSection quest = quests.getConfigurationSection(questReference);
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
				if (!npcQuests.get(npcName).contains(questReference)) {
					npcQuests.get(npcName).add(questReference);
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
				if (!mobQuests.get(mobName).contains(questReference)) {
					mobQuests.get(mobName).add(questReference);
				}
			}
		}
	}
	
	public static HashMap<String, PKAQuest> getQuestMap(List<String> questReferences, String playerName) {
		HashMap<String, PKAQuest> questsWithReferences = new HashMap<String, PKAQuest>();
		for (String questReference : questReferences) {
			questsWithReferences.put(questReference, ElementsUtil.getQuestElement(questReference, playerName));
		}
		return questsWithReferences;
	}
	
	private static PKAQuest getPKAQuestFromMob(PKAMob pkaMob, String playerName) {
		if (pkaMob.getQuestReferences() == null)
			return null;
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		if (pkaPlayer == null)
			return null;
		HashMap<String, PKAQuest> activeQuests = pkaPlayer.getActiveQuests();
		List<String> finishedQuests = pkaPlayer.getFinishedQuests();
		List<String> questReferences = pkaMob.getQuestReferences();
		for (String questReference : questReferences) {
			if (activeQuests.containsKey(questReference))
				return activeQuests.get(questReference);
			else if (finishedQuests.contains(questReference))
				continue;
			
			PKAQuest pkaQuest = ElementsUtil.getQuestElement(questReference, playerName);
			if (pkaQuest != null && !pkaQuest.getQuestState().equals(QuestState.FINISHED)) {
				pkaPlayer.addActiveQuest(questReference, pkaQuest);
				return pkaQuest;
			}
		}
		return null;
	}
	
	public static ConfigurationSection getQuestSection(String questReference) {
		return quests.getConfigurationSection(questReference);
	}
	
	private static void processQuestState(PKAQuest pkaQuest, ConfigurationSection questSection, boolean isAskingIfCompleted) {
		String playerName = pkaQuest.getPlayerName();
		QuestState questState = pkaQuest.getQuestState();
		
		if (questState == QuestState.PENDING) {
			if (!isAskingIfCompleted)
				return;
			ConfigurationSection questStateSection = questSection.getConfigurationSection("PENDING");
			MessageUtil.sendMessage(Bukkit.getPlayer(playerName), questStateSection.getString("send-message"), MessageType.SINGLE);
			InventoryUtil.giveItem(playerName, questStateSection.getString("give-item"), pkaQuest.getLevel());
			changeQuestState(pkaQuest, questSection, QuestState.ACCEPTED);
		} else if (questState == QuestState.ACCEPTED) {
			ConfigurationSection questStateSection = questSection.getConfigurationSection("ACCEPTED." + pkaQuest.getStage());
			processCompletion(pkaQuest, questSection, questStateSection, isAskingIfCompleted);
		} else if (questState == QuestState.COMPLETED) {
			if (!isAskingIfCompleted)
				return;
			ConfigurationSection questStateSection = questSection.getConfigurationSection("COMPLETED");
			MessageUtil.sendMessage(Bukkit.getPlayer(playerName), questStateSection.getString("send-message"), MessageType.SINGLE);
			InventoryUtil.giveItem(playerName, questStateSection.getString("give-item"), pkaQuest.getLevel());
			PlayerProcessor.rewardExperience(playerName, questStateSection.getInt("give-experience"));
			changeQuestState(pkaQuest, questSection, QuestState.FINISHED);
		} else if (questState == QuestState.FINISHED) {
			if (!isAskingIfCompleted)
				return;
			ConfigurationSection questStateSection = questSection.getConfigurationSection("FINISHED");
			MessageUtil.sendMessage(Bukkit.getPlayer(playerName), questStateSection.getString("send-message"), MessageType.SINGLE);
			setQuestAsFinished(pkaQuest.getPlayerName(), pkaQuest.getQuestReference());
		}
		
	}
	
	private static void processCompletion(PKAQuest pkaQuest, ConfigurationSection questSection, ConfigurationSection questStateSection, boolean isAskingIfCompleted) {
		if (!questStateSection.contains("completion-check"))
			return;
		Integer stage = pkaQuest.getStage();
		
		if (hasCompleted(pkaQuest, questSection, questStateSection.getConfigurationSection("completion-check"), stage)) {
			Integer newStage = stage + 1;
			if (changeQuestStage(pkaQuest, questSection, newStage)) {
				//quest proceeds to next section
				processQuestState(pkaQuest, questSection, isAskingIfCompleted);
			} else {
				//quest is fully completed
				changeQuestState(pkaQuest, questSection, QuestState.COMPLETED);
				processQuestState(pkaQuest, questSection, isAskingIfCompleted);
			}
		} else if (isAskingIfCompleted) {
			String failMessage = questStateSection.getString("fail-message");
			if (failMessage == null) {
				sendQuestIsMissingMessage(pkaQuest, "fail-message", "in one of its stages.");
				return;
			}
			MessageUtil.sendMessage(Bukkit.getPlayer(pkaQuest.getPlayerName()), failMessage, MessageType.SINGLE);
		}
	}
	
	private static boolean hasCompleted(PKAQuest pkaQuest, ConfigurationSection questSection, ConfigurationSection checkSection, Integer stage) {
		if (!checkSection.contains("required")) {
			sendQuestIsMissingMessage(pkaQuest, "required", "completion-check in one of its stages.");
			return false;
		}
		
		QuestCompletionType questCompletionType = pkaQuest.getQuestCompletionType();
		if (questCompletionType == QuestCompletionType.KILL_COUNT) {
			//either a count (of kills) or boolean 0 = false, 1 = true
			int requiredCounter = checkSection.getInt("required");
			if (pkaQuest.getCurrentCompletionCounter() != null) {
				int questCounter = (int) pkaQuest.getCurrentCompletionCounter();
				if (questCounter >= requiredCounter)
					return true;
			}
		} else if (questCompletionType == QuestCompletionType.KILL_SPECIFIC_MOB) {
			if (pkaQuest.getCurrentCompletionCounter() != null)
				return (boolean) pkaQuest.getCurrentCompletionCounter();
		} else if (questCompletionType == QuestCompletionType.COLLECT_ITEM) {
			String requiredItemReference = checkSection.getString("required");
			return InventoryUtil.hasItemByReference(requiredItemReference);
		} else if (questCompletionType == QuestCompletionType.VISIT_LOCATION) {
			String requiredLocation = checkSection.getString("required");
			List<String> currentLocations = LocationUtil.getCurrentLocations(pkaQuest.getPlayerName());
			return currentLocations.contains(requiredLocation);
		}
		return false;
	}
	
	/**
	 * 
	 * @param pkaQuest
	 * @param questSection
	 * @param newstage
	 * @return false if it was the last stage
	 */
	private static boolean changeQuestStage(PKAQuest pkaQuest, ConfigurationSection questSection, Integer newStage) {
		if (questSection.contains(newStage.toString())) {
			pkaQuest.setStage(newStage);
			String questCompletionTypeString = questSection.getConfigurationSection("ACCEPTED." + newStage).getString("completion-type");
			if (questCompletionTypeString == null) {
				MessageUtil.log("quest " + pkaQuest.getQuestReference() + " is missing completion-type in it's new stage");
				return true;
			}
			pkaQuest.setQuestCompletionType(QuestCompletionType.valueOf(questCompletionTypeString));
			return true;
		}
		return false;
	}
	
	private static void changeQuestState(PKAQuest pkaQuest, ConfigurationSection questSection, QuestState questState) {
		if (questState == null)
			return;
		if (questState == QuestState.ACCEPTED) {
			String questCompletionTypeString = questSection.getConfigurationSection("ACCEPTED." + 1).getString("completion-type");
			if (questCompletionTypeString == null) {
				MessageUtil.log("quest " + pkaQuest.getQuestReference() + " is missing completion-type in it's new stage");
				return;
			}
			pkaQuest.setQuestCompletionType(QuestCompletionType.valueOf(questCompletionTypeString));
		}
		pkaQuest.setQuestState(questState);
	}

	public static void npcClick(PKAMob pkaMob, String playerName, String clickFinger) {
		PKAQuest pkaQuest = getPKAQuestFromMob(pkaMob, playerName);
		if (pkaQuest == null)
			return;
		ConfigurationSection questSection = getQuestSection(pkaQuest.getQuestReference());
		
		processQuestState(pkaQuest, questSection, true);
	}
	
	public static void mobDeath(PKAMob pkaMob, String playerName) {
		PKAQuest pkaQuest = getPKAQuestFromMob(pkaMob, playerName);
		if (pkaQuest == null)
			return;
		ConfigurationSection questSection = getQuestSection(pkaQuest.getQuestReference());
		
		if (pkaQuest.getQuestCompletionType() == QuestCompletionType.KILL_COUNT) {
			int killCount = (int) pkaQuest.getCurrentCompletionCounter();
			pkaQuest.setCurrentCompletionCounter(killCount + 1);
			processQuestState(pkaQuest, questSection, false);
		} else if (pkaQuest.getQuestCompletionType() == QuestCompletionType.KILL_SPECIFIC_MOB) {
			pkaQuest.setCurrentCompletionCounter(true);
			processQuestState(pkaQuest, questSection, false);
		} else if (pkaQuest.getQuestCompletionType() == QuestCompletionType.COLLECT_ITEM) {
			dropItem(pkaQuest, questSection);
		}
	}
	
	private static void dropItem(PKAQuest pkaQuest, ConfigurationSection questSection) {
		if (!(pkaQuest.getQuestState() == QuestState.ACCEPTED))
			return;
		ConfigurationSection questStateSection = questSection.getConfigurationSection("ACCEPTED." + pkaQuest.getStage());
		String itemReference = questStateSection.getString("mob-drop");
		if (itemReference == null) {
			MessageUtil.log("quest " + pkaQuest.getQuestReference() + " is missing mob-drop in it's current stage");
			return;
		}
		InventoryUtil.giveItem(pkaQuest.getPlayerName(), itemReference, pkaQuest.getLevel());
	}
	
	private static void sendQuestIsMissingMessage(PKAQuest pkaQuest, String missingValue, String location) {
		MessageUtil.log("quest " + pkaQuest.getQuestReference() + " is missing " + missingValue + " in " + location);
	}
	
	private static void setQuestAsFinished(String playerName, String questReference) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(Bukkit.getPlayer(playerName));
		if (pkaPlayer == null)
			return;
		pkaPlayer.removeActiveQuest(questReference);
		pkaPlayer.addFinishedQuest(questReference);
	}
	
}
