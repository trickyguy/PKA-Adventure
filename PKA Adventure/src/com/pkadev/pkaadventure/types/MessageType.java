package com.pkadev.pkaadventure.types;

import com.pkadev.pkaadventure.utils.FileUtil;

public enum MessageType {

	SINGLE(FileUtil.getStringValueFromConfig(		FileUtil.getConfig(), 	"MessageType.SINGLE.prefix", 		"config.yml")),
	GROUP(FileUtil.getStringValueFromConfig(		FileUtil.getConfig(), 	"MessageType.GROUP.prefix", 		"config.yml")),
	SERVER(FileUtil.getStringValueFromConfig(		FileUtil.getConfig(), 	"MessageType.SERVER.prefix", 		"config.yml")),
	SINGLE_DEBUG(FileUtil.getStringValueFromConfig(	FileUtil.getConfig(), 	"MessageType.SINGLE_DEBUG.prefix", 	"config.yml")),
	GROUP_DEBUG(FileUtil.getStringValueFromConfig(	FileUtil.getConfig(), 	"MessageType.GROUP_DEBUG.prefix", 	"config.yml")),
	SERVER_DEBUG(FileUtil.getStringValueFromConfig(	FileUtil.getConfig(), 	"MessageType.SERVER_DEBUG.prefix", 	"config.yml")),
	SIMPLE("");

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
