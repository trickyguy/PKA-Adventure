package com.pkadev.pkaadventure.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class SidebarUtil {
	private static ScoreboardManager manager = null;
	
	public static void load() {
		manager = Bukkit.getScoreboardManager();
	}

	/**
	 * All sidebar thing is started this way. Now sit back and relax.
	 * @param player
	 * @return
	 */
	public static void loadSidebar(Player player, PKAPlayer pkaPlayer) {
		player.setScoreboard(getDefaultScoreBoard(player));
	}
	
	private static Scoreboard getDefaultScoreBoard(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return null;
		
		Scoreboard scoreBoard = manager.getNewScoreboard();
		Objective objective = scoreBoard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Duck loves you");
		
		Score goldAmount = objective.getScore("§6Gold:");
		goldAmount.setScore(pkaPlayer.getGoldAmount());
		Score playerAmount = objective.getScore("§7Players:");
		playerAmount.setScore(Bukkit.getOnlinePlayers().length);
		
		return scoreBoard;
	}
	
	private static Scoreboard getTeamScoreBoard(List<Player> players, String teamName) {
		Scoreboard scoreBoard = manager.getNewScoreboard();
		
		Objective objective = scoreBoard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(teamName);
		
		return scoreBoard;
	}
	
	private static Scoreboard getClassBoard(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return null;
		
		Scoreboard scoreBoard = manager.getNewScoreboard();
		Objective objective = scoreBoard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName(pkaPlayer.getClassType().toString());
		
		return scoreBoard;
	}
	
}
