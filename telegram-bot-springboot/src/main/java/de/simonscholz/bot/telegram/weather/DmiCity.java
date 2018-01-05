package de.simonscholz.bot.telegram.weather;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmiCity {
	private int id;

	private String label;

	private double longitude;

	private double latitude;

	private String country;

	private String country_code;
}
