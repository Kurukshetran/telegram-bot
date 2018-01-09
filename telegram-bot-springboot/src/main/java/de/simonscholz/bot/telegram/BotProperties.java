package de.simonscholz.bot.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("bot")
public class BotProperties {

	private String apiKey = "apikey";
	private String apiUrl = "apiurl";
	private int adminChatId = 0;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public int getAdminChatId() {
		return adminChatId;
	}

	public void setAdminChatId(int adminChatId) {
		this.adminChatId = adminChatId;
	}
}
