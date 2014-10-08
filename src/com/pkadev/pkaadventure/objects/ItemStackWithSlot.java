package com.pkadev.pkaadventure.objects;

import org.bukkit.inventory.ItemStack;

public class ItemStackWithSlot {

	private ItemStack itemStack;
	private Integer slot;

	public ItemStackWithSlot(ItemStack itemStack, Integer slot) {
		this.itemStack = itemStack;
		this.slot = slot;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}
	
	
}
