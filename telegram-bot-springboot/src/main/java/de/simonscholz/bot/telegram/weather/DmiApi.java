package de.simonscholz.bot.telegram.weather;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface DmiApi {
	
	static final String BASE_URL = "http://www.dmi.dk/Data4DmiDk/";
	
	static final String MODE_NOW = "dag1_2";

	static final String MODE_WEEK = "dag3_9";
	
	@GET("getData?type=forecast")
	Single<List<DmiCity>> getDmiCities(@Query("term") String cityName);

	@Streaming
	@GET("http://servlet.dmi.dk/byvejr/servlet/world_image")
	Single<ResponseBody> getWeatherImage(@Query("city") String cityId, @Query("mode") String mode);
	
	default String getWeatherImageUrl(String cityId, String mode) {
		// added System.currentTimeMillis() at the end of the image url, because telegram caches image urls
		return "http://servlet.dmi.dk/byvejr/servlet/world_image?city=" + cityId + "&mode=" + mode + "#" + System.currentTimeMillis();
	}
}
