package de.simonscholz.bot.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
	private double longitude;
	private double latitude;
}
