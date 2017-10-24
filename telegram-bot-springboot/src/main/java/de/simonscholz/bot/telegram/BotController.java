package de.simonscholz.bot.telegram;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.simonscholz.bot.telegram.api.TelegramResponse;
import de.simonscholz.bot.telegram.api.Update;
import de.simonscholz.bot.telegram.bot.TelegramBotClient;
import de.simonscholz.bot.telegram.handler.UpdateHandler;
import io.reactivex.Maybe;

@RestController
public class BotController {

	@Autowired
	private UpdateHandler handler;

	@Autowired
	private TelegramBotClient botClient;

	@Autowired
	private BotProperties botProperties;

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	public void webhook(@RequestBody Update update) {
		handler.handleUpdate(update);
	}

	@RequestMapping(value = "/setWebhook", method = RequestMethod.POST)
	public void setWebhook() {
		botClient.setWebhook(botProperties.getWebHookUrl());
	}

	@RequestMapping(value = "/handleUpdates", method = RequestMethod.GET)
	public void handleUpdates() {
		Maybe<TelegramResponse<Update>> updatesMaybe = botClient.getUpdates();
		updatesMaybe.subscribe(res -> {
			List<Update> updates = res.getResult();
			updates.stream().forEach(this::webhook);
		}, err -> {
			err.printStackTrace();
		});
	}
	
	@RequestMapping(value = "/printUpdates", method = RequestMethod.GET)
	public void getUpdates() {
		Maybe<TelegramResponse<Update>> updatesMaybe = botClient.getUpdates();
		updatesMaybe.subscribe(res -> {
			List<Update> updates = res.getResult();
			String collect = updates.stream().map(update -> update.getMessage().getText()).collect(Collectors.joining(","));
			System.out.println(collect);
		}, err -> {
			err.printStackTrace();
		});
	}
}
