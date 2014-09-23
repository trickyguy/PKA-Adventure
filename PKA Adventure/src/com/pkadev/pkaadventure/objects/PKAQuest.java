package com.pkadev.pkaadventure.objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pkadev.pkaadventure.types.QuestFailCompletionType;
import com.pkadev.pkaadventure.types.QuestState;

@Entity()
@Table(name="pkaadventure_pkaquest")
public class PKAQuest {

	@Id
	private int id;
	private String playerName;
	private String questName;
	private String questNameForBook;
	private QuestState questState;
	private QuestFailCompletionType questCompletionType;
	private int stage = -1;
	
	public PKAQuest() {}
	
	public PKAQuest(String questName, String playerName) {
		
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

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public String getQuestNameForBook() {
		return questNameForBook;
	}

	public void setQuestNameForBook(String questNameForBook) {
		this.questNameForBook = questNameForBook;
	}

	public QuestState getQuestState() {
		return questState;
	}

	public void setQuestState(QuestState questState) {
		this.questState = questState;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public QuestFailCompletionType getQuestCompletionType() {
		return questCompletionType;
	}

	public void setQuestCompletionType(QuestFailCompletionType questCompletionType) {
		this.questCompletionType = questCompletionType;
	}
	
	
}
