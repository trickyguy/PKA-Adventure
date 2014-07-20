package com.pkadev.pkaadventure.listeners;

import org.bukkit.event.Listener;

import com.pkadev.pkaadventure.Main;

public class JobListener implements Listener {

	private static JobListener i; private JobListener(){} public static JobListener i() {if (i == null)i = new JobListener();return i;}


}
