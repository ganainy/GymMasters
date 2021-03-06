package ganainy.dev.gymmasters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ganainy.dev.gymmasters.models.app_models.User;

public class SharedPrefUtils {
    public static final String IS_FIRST_SHOWING_OF_SIGN_UP = "isFirstShowingOfSignUp";
    public static final String IS_FIRST_SHOWING_OF_WORKOUT = "isFirstShowingOfWorkout";
    public static final String IS_FIRST_SHOWING_MAP = "isFirstShowingMap";
    public static final String SHOULD_UPDATE_POSTS = "shouldUpdatePosts";

    public static void putBoolean(Context context, Boolean value, String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context,String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        return pref.getBoolean(key,true);
    }

}
