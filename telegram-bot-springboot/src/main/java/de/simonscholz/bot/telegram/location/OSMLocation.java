package de.simonscholz.bot.telegram.location;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSMLocation {
	private String place_id;
	private OSMAddress address;
}