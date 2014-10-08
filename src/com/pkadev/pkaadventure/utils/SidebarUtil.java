package com.pkadev.pkaadventure.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.OfflinePlayerNoLookup;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.objects.PKATeam;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class SidebarUtil {
	private static Main plugin;
	private static ScoreboardManager manager = null;
	
	public static void load(Main instance) {
		plugin = instance;
		manager = Bukkit.getScoreboardManager();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(plugin.getName()), new Runnable() {

			@Override
			public void run() {
				updateScoreBoards();
			}
				
		}, 40L, 41L);
	}
	
	public static void loadPlayer(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		loadFreshScoreBoard(player, pkaPlayer);
		if (pkaTeam == null)
			return;
		loadFreshScoreBoards(pkaTeam);
	}
	
	public static void unloadPlayer(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam == null)
			return;
		loadFreshScoreBoards(pkaTeam);
	}
	
	private static void loadFreshScoreBoards(PKATeam pkaTeam) {
		for (PKAPlayer pkaPlayer : pkaTeam.getOnlinePlayers()) {
			loadFreshScoreBoard(pkaPlayer);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void updateScoreBoards() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateScoreBoard(player);
		}
	}
	
	/**
	 * All sidebar thing is started this way. Now sit back and relax.
	 * @param player
	 * @return
	 */
	private static void loadFreshScoreBoard(PKAPlayer pkaPlayer) {
		loadFreshScoreBoard(pkaPlayer.getPlayer(), pkaPlayer);
	}
	
	/**
	 * Used when players join and when they leave a team therefore having no team
	 * @param player
	 * @param pkaPlayer
	 */
	private static void loadFreshScoreBoard(Player player, PKAPlayer pkaPlayer) {
		player.setScoreboard(getDefaultScoreBoard(player, pkaPlayer));
	}
	
	@SuppressWarnings("deprecation")
	private static Scoreboard getDefaultScoreBoard(Player player, PKAPlayer pkaPlayer) {
		Scoreboard scoreBoard = manager.getNewScoreboard();
		Objective sidebar = scoreBoard.registerNewObjective("sidebar", "dummy");
		//Objective belowName = scoreBoard.registerNewObjective("undername", "dummy");
		Team team = null;
	
		sidebar.setDisplayName("§cInformation");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score goldAmount = sidebar.getScore("Gold");
		Score playerAmount = sidebar.getScore("Players");
		goldAmount.setScore(pkaPlayer.getGoldAmount());
		playerAmount.setScore(Bukkit.getOnlinePlayers().length);
		
		/*belowName.setDisplayName("");
		belowName.setDisplaySlot(DisplaySlot.BELOW_NAME);*/
		
		PKATeam pkaTeam = pkaPlayer.getPKATeam();
		if (pkaTeam != null) {
			team = scoreBoard.registerNewTeam(pkaPlayer.getPKATeam().getName());
			team.setPrefix("§a");
			
			for (PKAPlayer onlinePlayer : pkaTeam.getOnlinePlayers()) {
				team.addPlayer(new OfflinePlayerNoLookup(onlinePlayer.getName()));
			}
		}
		
		return scoreBoard;
	}
	
	@SuppressWarnings("deprecation")
	private static void updateScoreBoard(Player player) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		
		Scoreboard scoreBoard = player.getScoreboard();
		Objective sidebar = scoreBoard.getObjective(DisplaySlot.SIDEBAR);
		if (sidebar == null) {
			loadFreshScoreBoard(player, pkaPlayer);
			return;
		}
		
		Score goldAmount = sidebar.getScore("Gold");
		Score playerAmount = sidebar.getScore("Players");
		goldAmount.setScore(pkaPlayer.getGoldAmount());
		playerAmount.setScore(Bukkit.getOnlinePlayers().length);
	}
	
}
