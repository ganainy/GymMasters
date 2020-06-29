package ganainy.dev.gymmasters.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String PLACES_BASE_URL= "https://maps.googleapis.com/maps/api/place/findplacefromtext/";

    private static Retrofit retrofitPlacesClient;

    public static Retrofit getRetrofitPlacesClient(){
        if (retrofitPlacesClient ==null){
             retrofitPlacesClient = new Retrofit.Builder()
                    .baseUrl(PLACES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitPlacesClient;
    }



}
