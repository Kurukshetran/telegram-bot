package de.simonscholz.bot.telegram.location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSMLocation {
	private String place_id;
	private OSMAddress address;
}