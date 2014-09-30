package com.pkadev.pkaadventure.threads;

import java.util.ArrayList;
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
		ArrayList<BrokenOreBlock> temp = new ArrayList<BrokenOreBlock>();
		for(BrokenOreBlock oreBlock : BrokenOreBlock.getAllBlocks()) {
			int timeLeft = oreBlock.getTimeLeft();
			
			if(timeLeft == 0)
				temp.add(oreBlock);
			else {
				oreBlock.setTimeLeft(timeLeft -= 1);
			}
		}
		
		for(BrokenOreBlock broken : temp)
			SkillsUtil.removeBrokenOreBlock(broken);
	}
	
	public static void start() {
		BukkitTask ores = new OreTimer().runTaskTimer(plugin, 20L, 20L);
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
