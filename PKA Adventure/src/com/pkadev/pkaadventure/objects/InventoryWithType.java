package com.pkadev.pkaadventure.objects;

import org.bukkit.inventory.Inventory;

import com.pkadev.pkaadventure.types.InventoryType;

public class InventoryWithType {

	private Inventory inventory;
	private InventoryType inventoryType;
	private long lastOpened;
	
	public InventoryWithType(Inventory inventory, InventoryType inventoryType) {
		setInventory(inventory);
		setInventoryType(inventoryType);
		lastOpened = System.currentTimeMillis();
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	private void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	private void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}

	public long getLastOpened() {
		return lastOpened;
	}

	public void setLastOpened() {
		lastOpened = System.currentTimeMillis();
	}
	
}
