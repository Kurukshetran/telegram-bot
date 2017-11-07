package de.simonscholz.bot.telegram;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	public void webhook(@RequestBody Update update) {
		handler.handleUpdate(update);
	}

	@RequestMapping(value = "/poll", method = RequestMethod.GET)
	public String poll(@RequestParam(name="count", required=false, defaultValue="1") int count) {
		Maybe<TelegramResponse<Update>> updatesMaybe = botClient.getUpdates();
		updatesMaybe.subscribe(res -> {
			List<Update> updates = res.getResult();
			updates.stream().limit(count).forEach(this::webhook);
		}, err -> {
			err.printStackTrace();
		});
		return "Polling updates";
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
