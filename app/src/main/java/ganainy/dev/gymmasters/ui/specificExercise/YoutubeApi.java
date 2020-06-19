package ganainy.dev.gymmasters.ui.specificExercise;

import ganainy.dev.gymmasters.models.youtube_models.Example;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeApi {
    /**
     * this interface represents server in our app
     */
    @GET("search")
    Call<Example> getParentObject(
            @Query("part") String part,
            @Query("q") String q,
            @Query("type") String type,
            @Query("key") String key,
            @Query("maxResults") Integer maxResults
    );

}
