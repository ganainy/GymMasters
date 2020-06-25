package ganainy.dev.gymmasters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ganainy.dev.gymmasters.models.app_models.User;

public class SharedPrefUtils {
    public static final String IS_FIRST_SHOWING_OF_SIGN_UP = "isFirstShowingOfSignUp";
    public static final String LOGGED_USER="loggedUser";

    public static void putBoolean(Context context, Boolean value, String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context,String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        return pref.getBoolean(IS_FIRST_SHOWING_OF_SIGN_UP,true);
    }

    /**save logged user info in shared pref*/
    public static void putUser(Context context, User user){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        String userAsString = new Gson().toJson(user);
        editor.putString(LOGGED_USER,userAsString);
        editor.apply();
    }

    /**get logged user info*/
    public static User getUser(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        String userAsString = pref.getString(LOGGED_USER, null);
        return new Gson().fromJson(userAsString,User.class);
    }

}
