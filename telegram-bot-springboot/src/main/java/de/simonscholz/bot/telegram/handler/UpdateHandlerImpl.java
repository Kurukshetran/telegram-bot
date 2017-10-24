package de.simonscholz.bot.telegram.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.simonscholz.bot.telegram.api.Message;
import de.simonscholz.bot.telegram.api.Update;
import de.simonscholz.bot.telegram.bot.TelegramBotClient;
import de.simonscholz.bot.telegram.translate.Translation;
import de.simonscholz.bot.telegram.translate.TranslationApi;
import de.simonscholz.bot.telegram.weather.DmiApi;
import de.simonscholz.bot.telegram.weather.DmiCity;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Component
public class UpdateHandlerImpl implements UpdateHandler {

	private Logger LOG = LoggerFactory.getLogger(UpdateHandlerImpl.class);

	@Autowired
	private DmiApi dmiApi;

	@Autowired
	private TranslationApi translationApi;
	
	@Autowired
	private TelegramBotClient telegramBot;

	@Override
	public void handleUpdate(Update update) {
		Message message = update.getMessage();

		int chatId = message.getChat().getId();
		String text = message.getText();

		LOG.debug("Chat id:" + chatId);
		LOG.debug("Text : " + text);

		int indexOf = text.indexOf(" ");

		if (indexOf > -1) {
			String queryString = text.substring(indexOf);

			if (text.startsWith("/now")) {
				Single<List<DmiCity>> dmiCities = dmiApi.getDmiCities(queryString.trim());
				sendDmiPhoto(chatId, dmiCities, DmiApi.MODE_NOW);
			} else if (text.startsWith("/week")) {
				Single<List<DmiCity>> dmiCities = dmiApi.getDmiCities(queryString.trim());
				sendDmiPhoto(chatId, dmiCities, DmiApi.MODE_WEEK);
			} else if (text.startsWith("/de")) {
				Single<Translation> translation = translationApi.getTranslation(queryString, "de", "en");
				translation.subscribe(t -> {
					Maybe<Message> sendMessage = telegramBot.sendMessage(chatId, t.getTranslationText());
					sendMessage.subscribe(m -> {
						LOG.debug(m.getText());
					});
				});
			} else if (text.startsWith("/en")) {
				Single<Translation> translation = translationApi.getTranslation(queryString, "en", "de");
				translation.subscribe(t -> {
					Maybe<Message> sendMessage = telegramBot.sendMessage(chatId, t.getTranslationText());
					sendMessage.subscribe(m -> {
						LOG.debug(m.getText());
					});
				});
			}

		} else {
			Maybe<Message> sendMessage = telegramBot.sendMessage(chatId, "This is not a proper command. \n Please use /now or /week + city name or for translations /en or /de");
			sendMessage.subscribe(m -> {
				LOG.debug(m.getText());
			});
		}
	}

	private void sendDmiPhoto(int chatId, Single<List<DmiCity>> dmiCities, String mode) {
		dmiCities.subscribe(cities -> cities.stream().findFirst().ifPresent(dmiCity -> {
			String weatherImageUrl = dmiApi.getWeatherImageUrl(String.valueOf(dmiCity.getId()), mode);
			Maybe<Message> sendPhoto = telegramBot.sendPhoto(chatId, weatherImageUrl);
			sendPhoto.subscribe(m -> {
				LOG.debug(m.getText());
			});
		}));
	}

}
