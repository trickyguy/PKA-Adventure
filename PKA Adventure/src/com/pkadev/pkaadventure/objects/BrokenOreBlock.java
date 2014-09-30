package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BrokenOreBlock {
	
	//
	private static List<BrokenOreBlock> allBlocks = new ArrayList<BrokenOreBlock>();
	
	private Block oreBlock;
	private Material originalMaterial;
	private int timeLeft;
	
	public BrokenOreBlock(Block block, Material material, int time) {
		oreBlock = block;
		originalMaterial = material;
		timeLeft = time;
		
		allBlocks.add(this);
	}
	
	public static List<BrokenOreBlock> getAllBlocks() {
		return allBlocks;
	}
	
	public Block getOreBlock() {
		return oreBlock;
	}
	
	public Material getMaterial() {
		return originalMaterial;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(int time) {
		timeLeft = time;
	}
	
}
