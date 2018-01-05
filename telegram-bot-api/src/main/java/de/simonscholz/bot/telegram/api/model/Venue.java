package de.simonscholz.bot.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Venue {
	private Location location;
	private String title;
	private String address;
	private String foursquare_id;
}
