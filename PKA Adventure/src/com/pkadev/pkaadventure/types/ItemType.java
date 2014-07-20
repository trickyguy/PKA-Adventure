package com.pkadev.pkaadventure.types;

import com.pkadev.pkaadventure.utils.MathUtil;

public enum ItemType {

	SKILL(MathUtil.getInt("skill_drop_likelyhood"), 
			new String[]{"skill_item", "skill_level", "skill_exp", ""}, 
			new String[]{"skill_doubledrop_percent"}, 1),
	ARMOR(MathUtil.getInt("armor_drop_likelyhood"), 
			new String[]{"armor_item", "armor_level", ""}, 
			new String[]{"strength", "toughness", "toughness", "toughness", "agility", "restoration"}, 4);
	
	private final int likelyHood;
	
	//specific set of lines that will be added to the itemLore
	private final String[] elements;
	
	//elements that may vary in their number, and are put at the end of the item
	// or 1 above the end if the elements contain a level
	private final String[] endElements;
	//the amount of possible endElements to be added
	private final int maxEndElements;
	
	ItemType(int likelyHood, String[] elements, String[] endElements, int maxEndElements) {
		this.likelyHood = likelyHood;
		this.elements = elements;
		this.endElements = endElements;
		this.maxEndElements = maxEndElements;
	}
	
	public String[] getElements() {
		return elements;
	}
	
	public String[] getEndElements() {
		return endElements;
	}
	
	public int getMaxEndElements() {
		return maxEndElements;
	}
	
	public int getLikelyHood() {
		return likelyHood;
	}
}
