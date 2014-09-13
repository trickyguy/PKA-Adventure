package com.pkadev.pkaadventure.objects;

import java.util.List;

public class ItemType {
	
	public ItemType(List<String> elements, List<String> endElements, int maxEndElements) {
		this.elements = elements;
		this.endElements = endElements;
		this.maxEndElements = maxEndElements;
	}

	//specific set of lines that will be added to the itemLore
	private final List<String> elements;
	
	//elements that may vary in their number, and are put at the end of the item
	// or 1 above the end if the elements contain a level
	private final List<String> endElements;
	//the amount of possible endElements to be added
	private final int maxEndElements;
	
	public List<String> getElements() {
		return elements;
	}
	
	public List<String> getEndElements() {
		return endElements;
	}
	
	public int getMaxEndElements() {
		return maxEndElements;
	}
	
}
