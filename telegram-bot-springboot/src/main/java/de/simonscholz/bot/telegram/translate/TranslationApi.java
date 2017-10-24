package de.simonscholz.bot.telegram.translate;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslationApi {
	
	static final String BASE_URL = "http://transltr.org/api/";


	@GET("/translate")
	Single<Translation> getTranslation(@Query("text") String text, @Query("to") String toLanguage,
			@Query("from") String fromLanguage);
}
