package com.pkadev.pkaadventure.types;

import com.pkadev.pkaadventure.utils.FileUtil;

public enum MessageType {

	SINGLE(FileUtil.getStringValueFromConfig("MessageType.SINGLE.prefix")),
	GROUP(FileUtil.getStringValueFromConfig("MessageType.GROUP.prefix")),
	SERVER(FileUtil.getStringValueFromConfig("MessageType.SERVER.prefix")),
	SINGLE_DEBUG(FileUtil.getStringValueFromConfig("MessageType.SINGLE_DEBUG.prefix")),
	GROUP_DEBUG(FileUtil.getStringValueFromConfig("MessageType.GROUP_DEBUG.prefix")),
	SERVER_DEBUG(FileUtil.getStringValueFromConfig("MessageType.SERVER_DEBUG.prefix"));

	private final String prefix;

	/**
	 * @param prefix:      	Color and text of the prefix
	 */
	MessageType(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return				Will replace # with the next togglecolor starting at 0 and repeating if you have tons of them.
	 * 						Will return prefix + message if the MessageTpye doesn't have togglecolors, so feel free to use null
	 * 																							if you dont need togglecolors
	 */
	public String getFinalizedMessage(String message) {
		String finalmessage = prefix;
		return finalmessage += message;
	}

}
