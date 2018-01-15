package de.simonscholz.bot.telegram.handler;

import java.util.Date;
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
import de.simonscholz.bot.telegram.api.model.User;
import de.simonscholz.bot.telegram.entities.DmiLocation;
import de.simonscholz.bot.telegram.entities.Query;
import de.simonscholz.bot.telegram.location.OSMLocation;
import de.simonscholz.bot.telegram.location.OpenStreetMapApi;
import de.simonscholz.bot.telegram.repositories.DmiLocationRepository;
import de.simonscholz.bot.telegram.repositories.QueryRepository;
import de.simonscholz.bot.telegram.repositories.UserRepository;
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

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QueryRepository queryRepository;

	@Override
	public void handleUpdate(Update update) {
		Optional<Message> messageOptional = getMessage(update);

		messageOptional.ifPresent(message -> {

			saveQuery(message);

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
				} else if (text.startsWith("/start") || text.startsWith("/help")) {
					String username = getUserName(message);
					StringBuilder sb = new StringBuilder();
					sb.append("Hello ");
					sb.append(username);
					sb.append(",");
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
					sb.append("Nice to meet you. I am the Dmi.dk weather bot.");
					sb.append(System.lineSeparator());
					sb.append("I was developed by Simon Scholz, a java developer, located in Hamburg.");
					sb.append(System.lineSeparator());
					sb.append("My source code can be found here: https://github.com/SimonScholz/telegram-bot/");
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
					sb.append("But enough of this technical stuff.");
					sb.append(System.lineSeparator());
					sb.append("You wanna have these nice dmi.dk weather charts, right? ");
					sb.append(System.lineSeparator());
					sb.append(
							"You can get these by using the /now + {your home town name} or /week + {your home town name} or by simply sending me your location. ");
					sb.append(System.lineSeparator());
					sb.append(
							"The /now command shows the weather forecast for the next 3 days and the /week command is used for the week beginning after the next 3 days.");
					telegramBot.sendMessage(chatId, sb.toString()).subscribe();
				} else {
					Chat chat = message.getChat();
					if (Chat.TYPE_PRIVATE.equals(chat.getType())) {
						Maybe<Message> sendMessage = telegramBot.sendMessage(chatId,
								"This is not a proper command. \n You can send /help to get help.");
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

	private String getUserName(Message message) {
		String username = message.getFrom().getUsername();
		if (username != null && !username.isEmpty()) {
			return username;
		}
		username = message.getFrom().getFirst_name();

		if (username != null && !username.isEmpty()) {
			String lastName = message.getFrom().getLast_name();
			if(lastName != null && !lastName.isEmpty()) {
				return username + " " + lastName;
			}
			return username;
		}

		return message.getChat().getUsername();
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
		}), err -> {
			LOG.error(err.getMessage(), err);
		});
	}

	private void sendDmiPhoto(long chatId, String mode, int dmiCityId) {
		String weatherImageUrl = dmiApi.getWeatherImageUrl(String.valueOf(dmiCityId), mode);
		Maybe<Message> sendPhoto = telegramBot.sendPhoto(chatId, weatherImageUrl);
		sendPhoto.subscribe(m -> {
			LOG.debug(m.getText());
		});
	}

	private void saveQuery(Message message) {
		User from = message.getFrom();
		Optional<de.simonscholz.bot.telegram.entities.User> userOptional = userRepository
				.findUserByTelegramId(from.getId());

		de.simonscholz.bot.telegram.entities.User user = userOptional.orElseGet(() -> {
			de.simonscholz.bot.telegram.entities.User newUser = new de.simonscholz.bot.telegram.entities.User();
			newUser.setTelegramId(from.getId());
			newUser.setFirst_name(from.getFirst_name());
			newUser.setLast_name(from.getLast_name());
			newUser.setUsername(from.getUsername());
			newUser.set_bot(from.is_bot());
			newUser.setLanguageCode(from.getLanguage_code());

			userRepository.save(newUser);

			return newUser;
		});
		Query query = new Query();
		query.setUser(user);
		query.setMessage(message.getText());
		Location location = message.getLocation();
		if (location != null) {
			query.setLatitude(location.getLatitude());
			query.setLongitude(location.getLongitude());
		}
		int date = message.getDate();
		query.setDate(new Date(date));

		queryRepository.save(query);
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
