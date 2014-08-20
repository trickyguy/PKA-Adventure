package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.pkadev.pkaadventure.interfaces.MobMonster;
import com.pkadev.pkaadventure.types.MobStance;
import com.pkadev.pkaadventure.types.MobStrength;
import com.pkadev.pkaadventure.types.MobType;
import com.pkadev.pkaadventure.types.SpawnNodeType;

public class SpawnNode {

	/**
	 * mob
	 * @param location
	 * @param radius
	 * @param level
	 * @param mob
	 * @param mobStrength
	 * @param mobStance
	 */
	public SpawnNode(Location location, String name, int radius, int level, 
			int amount, String mob, 
			MobStrength mobStrength, MobStance mobStance, MobType mobType) {
		setSpawnNodeType(SpawnNodeType.MOB);
		setName(name);
		setLocation(location);
		setRadius(radius);
		setLevel(level);
		setAmount(amount);
		setTicksToRefresh(ticksToRefresh);
		setMob(mob);
		setMobStrength(mobStrength);
		setMobStance(mobStance);
		setMobType(mobType);
	}
	
	/**
	 * beacon
	 * @param location
	 */
	public SpawnNode(Location location, String name) {
		setSpawnNodeType(SpawnNodeType.BEACON);
		setLocation(location);
		setName(name);
	}
	
	/**
	 * lootcrate
	 * @param location
	 * @param radius
	 * @param level
	 */
	public SpawnNode(Location location) {
		setSpawnNodeType(SpawnNodeType.LOOTCRATE);
		setLocation(location);
	}
	
	private SpawnNodeType spawnNodeType;
	private Location location;
	private int radius;
	private int level;
	private int amount;
	private int ticksToRefresh;
	private String name;
	private String mob;
	private MobStrength mobStrength;
	private MobStance mobStance;
	private MobType mobType;
	
	private List<MobMonster> liveMobs = new ArrayList<MobMonster>(); //TODO
	
	
	public SpawnNodeType getSpawnNodeType() {
		return spawnNodeType;
	}
	public void setSpawnNodeType(SpawnNodeType spawnNodeType) {
		this.spawnNodeType = spawnNodeType;
	}
	public Location getLocation() {
		return location;
	}
	private void setLocation(Location location) {
		this.location = location;
	}
	public int getRadius() {
		return radius;
	}
	private void setRadius(int radius) {
		this.radius = radius;
	}
	public int getLevel() {
		return level;
	}
	private void setLevel(int level) {
		this.level = level;
	}
	public int getAmount() {
		return amount;
	}
	private void setAmount(int amount) {
		this.amount = amount;
	}
	public int getTicksToRefresh() {
		return ticksToRefresh;
	}
	public void setTicksToRefresh(int ticksToRefresh) {
		this.ticksToRefresh = ticksToRefresh;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMob() {
		return mob;
	}
	private void setMob(String mob) {
		this.mob = mob;
	}
	public int getLiveMobAmount() {
		return liveMobs.size();
	}
	public void addLiveMob(MobMonster mobMonster) {
		liveMobs.add(mobMonster);
	}
	public List<MobMonster> getLiveMobs() {
		return liveMobs;
	}
	public MobStrength getMobStrength() {
		return mobStrength;
	}
	private void setMobStrength(MobStrength mobStrength) {
		this.mobStrength = mobStrength;
	}
	public MobStance getMobStance() {
		return mobStance;
	}
	private void setMobStance(MobStance mobStance) {
		this.mobStance = mobStance;
	}
	public MobType getMobType() {
		return mobType;
	}
	public void setMobType(MobType mobType) {
		this.mobType = mobType;
	}
}
