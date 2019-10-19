package com.example.myapplication.places_model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {
    /**
     * this interface represents server in our app
     */
    @GET("json")
    Call<Candidates> getParentObject(
            @Query("input") String placeType, //gym
            @Query("inputtype") String textquery,
            @Query("fields") String fields,//geometry/location,formatted_address,name,opening_hours,rating
            @Query("locationbias") String location, //circle:2000@lat,lng
            @Query("key") String ApiKey
    );

}
