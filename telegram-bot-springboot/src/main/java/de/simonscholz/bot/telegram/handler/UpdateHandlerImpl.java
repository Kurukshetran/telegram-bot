package de.simonscholz.bot.telegram.handler;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.simonscholz.bot.telegram.api.TelegramBotClient;
import de.simonscholz.bot.telegram.api.model.Chat;
import de.simonscholz.bot.telegram.api.model.Location;
import de.simonscholz.bot.telegram.api.model.Message;
import de.simonscholz.bot.telegram.api.model.Update;
import de.simonscholz.bot.telegram.entities.DmiLocation;
import de.simonscholz.bot.telegram.location.OSMLocation;
import de.simonscholz.bot.telegram.location.OpenStreetMapApi;
import de.simonscholz.bot.telegram.repositories.DmiLocationRepository;
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
	private TelegramBotClient telegramBot;

	@Autowired
	private OpenStreetMapApi locationApi;

	@Autowired
	private DmiLocationRepository locationRepository;

	@Override
	public void handleUpdate(Update update) {
		Optional<Message> messageOptional = getMessage(update);

		messageOptional.ifPresent(message -> {

			long chatId = message.getChat().getId();
			String text = message.getText();
			Location location = message.getLocation();

			if (text != null) {
				LOG.debug("Chat id:" + chatId);
				LOG.debug("Text : " + text);

				int indexOf = text.indexOf(" ");

				if (indexOf > -1) {
					String queryString = text.substring(indexOf);

					if (text.startsWith("/now")) {
						Optional<DmiLocation> findByState = locationRepository
								.findByLabelContaining(queryString.trim());
						if (findByState.isPresent()) {
							DmiLocation dmiLocation = findByState.get();
							sendDmiPhoto(chatId, DmiApi.MODE_NOW, dmiLocation.getDmiId());
						} else {
							Single<List<DmiCity>> dmiCities = dmiApi.getDmiCities(queryString.trim());
							sendDmiPhoto(chatId, dmiCities, DmiApi.MODE_NOW);
						}
					} else if (text.startsWith("/week")) {
						Optional<DmiLocation> findByState = locationRepository
								.findByLabelContaining(queryString.trim());
						if (findByState.isPresent()) {
							DmiLocation dmiLocation = findByState.get();
							sendDmiPhoto(chatId, DmiApi.MODE_WEEK, dmiLocation.getDmiId());
						} else {
							Single<List<DmiCity>> dmiCities = dmiApi.getDmiCities(queryString.trim());
							sendDmiPhoto(chatId, dmiCities, DmiApi.MODE_WEEK);
						}
					}
				} else if (text.startsWith("/chatid")) {
					long id = message.getChat().getId();
					telegramBot.sendMessage(id, "Your chat id is: " + id).subscribe();
				} else {
					Chat chat = message.getChat();
					if (Chat.TYPE_PRIVATE.equals(chat.getType())) {
						Maybe<Message> sendMessage = telegramBot.sendMessage(chatId,
								"This is not a proper command. \n Please use /now or /week + city name");
						sendMessage.subscribe(m -> {
							LOG.debug(m.getText());
						});
					}
				}
			} else if (location != null) {
				Single<OSMLocation> locationData = locationApi.getLocationData(location.getLatitude(),
						location.getLongitude());
				locationData.subscribe(l -> {
					Optional<DmiLocation> findByState = locationRepository
							.findByLabelContaining(l.getAddress().getState());
					if (findByState.isPresent()) {
						DmiLocation dmiLocation = findByState.get();
						sendDmiPhoto(chatId, DmiApi.MODE_NOW, dmiLocation.getDmiId());
					} else {
						Single<List<DmiCity>> dmiCities = dmiApi.getDmiCities(l.getAddress().getState());
						sendDmiPhoto(chatId, dmiCities, DmiApi.MODE_NOW);
					}
				});
			}
		});
	}

	private Optional<Message> getMessage(Update update) {
		if (update.getMessage() != null) {
			return Optional.of(update.getMessage());
		} else if (update.getEdited_message() != null) {
			return Optional.of(update.getEdited_message());
		} else if (update.getChannel_post() != null) {
			return Optional.of(update.getChannel_post());
		} else if (update.getEdited_channel_post() != null) {
			return Optional.of(update.getEdited_channel_post());
		}

		return Optional.empty();
	}

	private void sendDmiPhoto(long chatId, Single<List<DmiCity>> dmiCities, String mode) {
		dmiCities.subscribe(cities -> cities.stream().findFirst().ifPresent(dmiCity -> {
			saveDmiCity(dmiCity);
			sendDmiPhoto(chatId, mode, dmiCity.getId());
		}));
	}

	private void sendDmiPhoto(long chatId, String mode, int dmiCityId) {
		String weatherImageUrl = dmiApi.getWeatherImageUrl(String.valueOf(dmiCityId), mode);
		Maybe<Message> sendPhoto = telegramBot.sendPhoto(chatId, weatherImageUrl);
		sendPhoto.subscribe(m -> {
			LOG.debug(m.getText());
		});
	}

	private void saveDmiCity(DmiCity dmiCity) {
		DmiLocation dmiLocation = new DmiLocation();
		dmiLocation.setDmiId(dmiCity.getId());
		dmiLocation.setLabel(dmiCity.getLabel());
		dmiLocation.setLatitude(dmiCity.getLatitude());
		dmiLocation.setLongitude(dmiCity.getLongitude());
		dmiLocation.setCountry(dmiCity.getCountry());
		dmiLocation.setCountry_code(dmiCity.getCountry_code());

		locationRepository.save(dmiLocation);
	}

}
