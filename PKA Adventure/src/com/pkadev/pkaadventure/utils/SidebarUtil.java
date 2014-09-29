package com.pkadev.pkaadventure.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.PKATeam;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class SidebarUtil {
	private static Main plugin;
	private static ScoreboardManager manager = null;
	private static boolean isScoreBoardDefault = true;
	
	public static void load(Main instance) {
		plugin = instance;
		manager = Bukkit.getScoreboardManager();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {

			@Override
			public void run() {
				toggleScoreBoards();
			}
				
		}, 80L, 1L);
	}

	/**
	 * All sidebar thing is started this way. Now sit back and relax.
	 * @param player
	 * @return
	 */
	public static void loadScoreBoards(PKAPlayer pkaPlayer) {
		loadScoreBoards(pkaPlayer.getPlayer(), pkaPlayer);
	}
	
	private static void loadScoreBoards(Player player, PKAPlayer pkaPlayer) {
		player.setScoreboard(getDefaultScoreBoard(player, pkaPlayer));
	}
	
	/**
	 * used for reasons: class change, gold pickup (will do nothing if isScoreBoardDefault == false)
	 * @param player
	 * @param pkaPlayer
	 */
	public static void updateScoreBoards(PKAPlayer pkaPlayer) {
		updateScoreBoards(pkaPlayer.getPlayer(), pkaPlayer);
	}

	private static void updateScoreBoards(Player player, PKAPlayer pkaPlayer) {
		if (isScoreBoardDefault)
			updateDefaultScoreBoard(player, pkaPlayer);
	}	
	
	private static void toggleScoreBoards() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			toggleScoreBoard(player);
		}
		if (isScoreBoardDefault)
			isScoreBoardDefault = false;
		else {
			isScoreBoardDefault = true;
		}
	}
	
	private static void toggleScoreBoard(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		toggleScoreBoard(player, pkaPlayer);
	}
	
	private static void toggleScoreBoard(Player player, PKAPlayer pkaPlayer) {
		if (isScoreBoardDefault) {
			player.setScoreboard(getDefaultScoreBoard(player, pkaPlayer));
		} else {
			player.setScoreboard(getTeamScoreBoard(player, pkaPlayer));
		}
	}
	
	private static Scoreboard getDefaultScoreBoard(Player player, PKAPlayer pkaPlayer) {
		Scoreboard scoreBoard = manager.getNewScoreboard();
		Objective objective = scoreBoard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Duck loves you");
		
		updateDefaultObjective(objective, pkaPlayer);
		
		return scoreBoard;
	}
	
	private static Scoreboard getTeamScoreBoard(Player player, PKAPlayer pkaPlayer) {
		Scoreboard scoreBoard = manager.getNewScoreboard();
		
		Objective objective = scoreBoard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(pkaPlayer.getPKATeam().getName());
		
		updateTeamObjective(objective, pkaPlayer.getPKATeam());
		
		return scoreBoard;
	}
	
	private static void updateClassObjective(Objective objective, Location location) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distanceSquared(location) >= 144)
				continue;
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
			if (pkaPlayer == null)
				continue;
			Score playerScore = objective.getScore("§a" + pkaPlayer.getName());
			playerScore.setScore((int) pkaPlayer.getHealth());
		}
	}
	
	private static void updateDefaultScoreBoard(Player player, PKAPlayer pkaPlayer) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		updateDefaultObjective(objective, pkaPlayer);
	}
	
	private static void updateDefaultObjective(Objective objective, PKAPlayer pkaPlayer) {
		Score goldAmount = objective.getScore("§6Gold:");
		goldAmount.setScore(pkaPlayer.getGoldAmount());
		Score playerAmount = objective.getScore("§7Players:");
		playerAmount.setScore(Bukkit.getOnlinePlayers().length);
	}
	
	private static void updateTeamScoreBoard(Player player, PKAPlayer pkaPlayer) {
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null)
			return;
		
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		updateTeamObjective(objective, pkaTeam);
	}
	
	private static void updateTeamObjective(Objective objective, PKATeam pkaTeam) {
		for (PKAPlayer pkaPlayer : pkaTeam.getPlayers()) {
			Score playerScore = objective.getScore(pkaPlayer.getName());
			playerScore.setScore((int) pkaPlayer.getHealth());
		}
	}
	
}
