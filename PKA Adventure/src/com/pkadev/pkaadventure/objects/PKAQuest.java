package com.pkadev.pkaadventure.objects;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pkadev.pkaadventure.types.QuestCompletionType;
import com.pkadev.pkaadventure.types.QuestState;

@Entity()
@Table(name="pkaadventure_pkaquest")
public class PKAQuest {

	@Id
	private int id;
	private String playerName;
	private String questReference;
	private QuestState questState;
	private QuestCompletionType questCompletionType;
	//used to count all kinds of things (kills of things etc, per stage)
	private Map<Integer, Object> completionCounter = new HashMap<Integer, Object>();
	private Integer stage;
	private int level;
	
	public PKAQuest() {}
	
	public PKAQuest(String questReference, String playerName, int level, QuestCompletionType questCompletionType) {
		setQuestReference(questReference);
		setPlayerName(playerName);
		setLevel(level);
		setStage(1);
		setQuestCompletionType(questCompletionType);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getQuestReference() {
		return questReference;
	}

	public void setQuestReference(String questReference) {
		this.questReference = questReference;
	}

	public QuestState getQuestState() {
		return questState;
	}

	public void setQuestState(QuestState questState) {
		this.questState = questState;
	}

	public Map<Integer, Object> getCompletionCounter() {
		return completionCounter;
	}
	
	public Object getCurrentCompletionCounter() {
		return completionCounter.get(stage);
	}

	public void setCompletionCounter(Map<Integer, Object> completionCounter) {
		this.completionCounter = completionCounter;
	}
	
	public void setCurrentCompletionCounter(Object obj) {
		completionCounter.put(stage, obj);
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public QuestCompletionType getQuestCompletionType() {
		return questCompletionType;
	}

	public void setQuestCompletionType(QuestCompletionType questCompletionType) {
		this.questCompletionType = questCompletionType;
	}
	
	
}
