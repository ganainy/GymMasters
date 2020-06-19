package ganainy.dev.gymmasters.ui.specificExercise.youtubeFragment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.youtube_models.Example;
import ganainy.dev.gymmasters.models.youtube_models.Id;
import ganainy.dev.gymmasters.models.youtube_models.Item;
import ganainy.dev.gymmasters.ui.specificExercise.YoutubeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeViewModel extends ViewModel {

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String SNIPPET = "snippet";
    public static final String VIDEO = "video";
    private static final String TAG = "YoutubeViewModelTag";

    private Application app;
    private String mVideoId;

    public Float getVideoCurrentSecond() {
        return mVideoCurrentSecond;
    }

    public void setVideoCurrentSecond(Float mVideoCurrentSecond) {
        this.mVideoCurrentSecond = mVideoCurrentSecond;
    }

    private Float mVideoCurrentSecond=0f;

    public String getExerciseName() {
        return mExerciseName;
    }

    private String mExerciseName;

    public YoutubeViewModel(Application app) {
        this.app = app;
    }

    public LiveData<String> getVideoIdLiveData() {
        return videoIdLiveData;
    }

    private MutableLiveData<String> videoIdLiveData = new MutableLiveData<>();


    /**get video id from youtube api that matches exercise name to be shown to user*/
    public void getExerciseVideoId(String exerciseName) {
        mExerciseName=exerciseName;

        if (mVideoId!=null)return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        YoutubeApi youtubeApi = retrofit.create(YoutubeApi.class);

        Call<Example> call = youtubeApi.getParentObject(
                SNIPPET,
                exerciseName,
                VIDEO,
                app.getString(R.string.youtube_v3_key),
                1);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.isSuccessful()) {
                    Example body = response.body();
                    List<Item> items = body.getItems();
                    for (Item item : items) {
                        Id id = item.getId();
                        mVideoId = id.getVideoId();
                        Log.i(TAG, "onResponse: " + mVideoId);
                    }
                    videoIdLiveData.setValue(mVideoId);

                } else {
                    Log.i(TAG, "onResponse: " + response.toString());
                    videoIdLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
                videoIdLiveData.setValue(null);
            }
        });
    }



}