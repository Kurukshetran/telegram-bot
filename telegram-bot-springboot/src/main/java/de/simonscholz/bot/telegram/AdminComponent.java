package de.simonscholz.bot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.simonscholz.bot.telegram.api.TelegramBotClient;
import de.simonscholz.bot.telegram.api.model.Message;
import io.reactivex.Maybe;

@Component
@Profile("admin")
public class AdminComponent {

	@Autowired
	private BotProperties botProperties;

	@Autowired
	private TelegramBotClient botClient;

	@Scheduled(cron = "${bot.admin-dyndns-renew-cron}")
	public void sendRenewDynDnsReminder() {
		int chatId = botProperties.getAdminChatId();
		Maybe<Message> sendMessage = botClient.sendMessage(chatId, "You need to renew the dyn dns account");
		sendMessage.subscribe();
	}

	@Scheduled(cron = "${bot.admin-dyndns-renew-ssl}")
	public void sendRenewSSLReminder() {
		int chatId = botProperties.getAdminChatId();
		Maybe<Message> sendMessage = botClient.sendMessage(chatId, "You need to renew your ssl certificate");
		sendMessage.subscribe();
	}
}
