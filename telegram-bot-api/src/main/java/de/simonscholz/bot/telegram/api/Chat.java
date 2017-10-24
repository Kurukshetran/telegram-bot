package de.simonscholz.bot.telegram.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat {
	private int id;
	private String type;
	private String title;
	private String username;
}
