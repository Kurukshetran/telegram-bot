package de.simonscholz.bot.telegram.location;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenStreetMapApi {
	static final String BASE_URL = "http://nominatim.openstreetmap.org/";

	@GET("reverse?format=jsonv2")
	Single<OSMLocation> getLocationData(@Query("lat") double latitude, @Query("lon") double longitude);
}
