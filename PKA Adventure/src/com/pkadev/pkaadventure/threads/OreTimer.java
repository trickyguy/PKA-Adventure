package com.pkadev.pkaadventure.threads;

import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.BrokenOreBlock;
import com.pkadev.pkaadventure.utils.SkillsUtil;

public class OreTimer extends BukkitRunnable {
	
	private static Main plugin = Main.instance;
	public static HashMap<String, BukkitTask> thread = new HashMap<String, BukkitTask>();
	private static String id = "ores";
	private static boolean running = false;
	
	public void run() {
		
		for(BrokenOreBlock oreBlock : BrokenOreBlock.getAllBlocks()) {
			int timeLeft = oreBlock.getTimeLeft();
			
			if(timeLeft == 0)
				SkillsUtil.removeBrokenOreBlock(oreBlock);
			else {
				oreBlock.setTimeLeft(timeLeft -= 1);
			}
		}
	}
	
	public static void start() {
		BukkitTask ores = new OreTimer().runTaskTimerAsynchronously(plugin, 20L, 20L);
		thread.put(id, ores);
		running = true;
	}

	public static void stop() {
		BukkitTask ores = thread.get(id);
		if(isRunning()) {
			ores.cancel();
			thread.remove(id);
			running = false;
		}
	}

	public static void restart() {
		stop();
		start();
	}
	
	public static boolean isRunning() {
		return running;
	}
}
