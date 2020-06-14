package ganainy.dev.gymmasters.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;

public class AuthUtils {

    private static String loggedUserId;

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
}
