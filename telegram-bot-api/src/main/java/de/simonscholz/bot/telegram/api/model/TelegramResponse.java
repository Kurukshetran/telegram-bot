package de.simonscholz.bot.telegram.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse<T> {
	private boolean ok;
	private List<T> result;
}
