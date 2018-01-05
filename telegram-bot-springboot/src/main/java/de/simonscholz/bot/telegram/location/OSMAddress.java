package de.simonscholz.bot.telegram.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSMAddress {
	private String road;
	private String house_number;
	private String suburb;
	private String city_district;
	private String postcode;
	private String state;
	private String country;
	private String country_code;
}
