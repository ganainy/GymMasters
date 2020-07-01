package ganainy.dev.gymmasters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import ganainy.dev.gymmasters.models.app_models.User;

public class AuthUtils {

    /**to set loggedUserId null on signout*/
    public static void setLoggedUserId(String loggedUserId) {
        AuthUtils.loggedUserId = loggedUserId;
    }

    private static String loggedUserId;
    public static final String LOGGED_USER="loggedUser";


    public static String getLoggedUserId(Context context){
        if (loggedUserId==null){
            if (FirebaseAuth.getInstance().getUid() != null) {
                loggedUserId= FirebaseAuth.getInstance().getUid();
            } else if (GoogleSignIn.getLastSignedInAccount(context) != null) {
                loggedUserId= GoogleSignIn.getLastSignedInAccount(context).getId();
            }
        }
        return loggedUserId;
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
