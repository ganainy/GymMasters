package ganainy.dev.gymmasters.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    public static final String isFirstShowingOfSignUp = "isFirstShowingOfSignUp";

    public static void putBoolean(Context context, Boolean value, String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context,String key){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        return pref.getBoolean(isFirstShowingOfSignUp,true);
    }

}
