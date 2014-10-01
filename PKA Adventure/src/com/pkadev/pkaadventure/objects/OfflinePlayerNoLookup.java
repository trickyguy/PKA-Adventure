package com.pkadev.pkaadventure.objects;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public final class OfflinePlayerNoLookup implements OfflinePlayer {
	//Cheers to Karl aka. TheBigDolphin1 aka. that guy that likes bananas


	private String username;
	private UUID uuid;

	public OfflinePlayerNoLookup(String username) {
		this(username, null);
	}
	public OfflinePlayerNoLookup(String username, UUID uuid) {
		this.username = username;
		this.uuid = uuid;
	}




	@Override
	public String getName() {
		return this.username;
	}
	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}


	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean arg0) {}

	@Override
	public Map<String, Object> serialize() {
		return null;
	}

	@Override
	public Location getBedSpawnLocation() {
		return null;
	}

	@Override
	public long getFirstPlayed() {
		return 0;
	}

	@Override
	public long getLastPlayed() {
		return 0;
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isBanned() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		return false;
	}

	@Override
	public void setBanned(boolean arg0) {}

	@Override
	public void setWhitelisted(boolean arg0) {}


	private static String md5String(String str) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {}
		return null;
	}

}
