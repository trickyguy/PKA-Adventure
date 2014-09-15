package com.pkadev.pkaadventure.objects;

public class AbilityWithTimers {

	private int[] timers;
	
	public void initialize(int numberOfTimers) {
		timers = new int[numberOfTimers];
	}
	
	public int getTimerAmount() {
		return timers.length;
	}
	
	public int [] getTimers() {
		return timers;
	}
	
	public void addTimer(int task, int slot) {
		timers[slot] = task;
	}
	
}
