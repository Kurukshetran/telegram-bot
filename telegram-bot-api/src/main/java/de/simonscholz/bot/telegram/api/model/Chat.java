package de.simonscholz.bot.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat {
	
	public static final String TYPE_PRIVATE = "private";
	public static final String TYPE_GROUP = "group";
	public static final String TYPE_SUPERGROUP = "supergroup";
	public static final String TYPE_CHANNEL = "channel";
	
	private int id;
	private String type;
	private String title;
	private String username;
}
