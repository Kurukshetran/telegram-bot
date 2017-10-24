package de.simonscholz.bot.telegram.bot;

import de.simonscholz.bot.telegram.api.Message;
import de.simonscholz.bot.telegram.api.TelegramResponse;
import de.simonscholz.bot.telegram.api.Update;
import io.reactivex.Maybe;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TelegramBotClient {
	
	@POST("sendMessage")
	@FormUrlEncoded
	Maybe<Message> sendMessage(@Field("chat_id") int chatId, @Field("text") String text);

	@POST("sendPhoto")
	@FormUrlEncoded
	Maybe<Message> sendPhoto(@Field("chat_id") int chatId, @Field("photo") String photoUrl);

	@GET("getUpdates")
	Maybe<TelegramResponse<Update>> getUpdates();
}
