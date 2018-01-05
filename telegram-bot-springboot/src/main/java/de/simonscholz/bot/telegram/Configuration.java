package de.simonscholz.bot.telegram;

import org.springframework.context.annotation.Bean;

import de.simonscholz.bot.telegram.api.TelegramBotClient;
import de.simonscholz.bot.telegram.location.OpenStreetMapApi;
import de.simonscholz.bot.telegram.weather.DmiApi;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@org.springframework.context.annotation.Configuration
public class Configuration {

	@Bean
	public Builder retrofitBuilder() {
		return new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
	}

	@Bean
	public DmiApi dmiApi(Builder builder) {
		return builder.baseUrl(DmiApi.BASE_URL).build().create(DmiApi.class);
	}

	@Bean
	public OpenStreetMapApi locationApi(Builder builder) {
		return builder.baseUrl(OpenStreetMapApi.BASE_URL).build().create(OpenStreetMapApi.class);
	}

	@Bean
	public TelegramBotClient telegramBbot(Builder builder, BotProperties botProperties) {
		return builder.baseUrl(botProperties.getApiUrl() + botProperties.getApiKey() + "/").build()
				.create(TelegramBotClient.class);
	}

}
