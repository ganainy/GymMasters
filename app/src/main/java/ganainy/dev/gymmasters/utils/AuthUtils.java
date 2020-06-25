package ganainy.dev.gymmasters.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ganainy.dev.gymmasters.models.app_models.User;

import static ganainy.dev.gymmasters.ui.userInfo.UserInfoViewModel.USERS;

public class AuthUtils {

    private static String loggedUserId;

    public static User getLoggedUser() {
        return loggedUser;
    }

    private static User loggedUser;

    //todo on login update loggeduser and id

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

    public static void setLoggedUser(Context context){
        FirebaseDatabase.getInstance().getReference().child(USERS).child(getLoggedUserId(context)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loggedUser=snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
