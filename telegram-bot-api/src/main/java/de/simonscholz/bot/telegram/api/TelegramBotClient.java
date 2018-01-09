package de.simonscholz.bot.telegram.api;

import de.simonscholz.bot.telegram.api.model.Message;
import de.simonscholz.bot.telegram.api.model.TelegramResponse;
import de.simonscholz.bot.telegram.api.model.Update;
import io.reactivex.Maybe;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TelegramBotClient {
	
	@POST("sendMessage")
	@FormUrlEncoded
	Maybe<Message> sendMessage(@Field("chat_id") long chatId, @Field("text") String text);

	@POST("sendPhoto")
	@FormUrlEncoded
	Maybe<Message> sendPhoto(@Field("chat_id") long chatId, @Field("photo") String photoUrl);

	@GET("getUpdates")
	Maybe<TelegramResponse<Update>> getUpdates();

	@GET("getUpdates")
	Maybe<TelegramResponse<Update>> getUpdates(@Query("offset") int offset);
}
