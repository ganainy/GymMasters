package ganainy.dev.gymmasters.ui.login;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private Application app;
    public NetworkChangeReceiver receiver;

    String email = "";
    String password = "";

    private MutableLiveData<Event<String>> emailErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<String>> passwordErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> navigateToMainLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Pair<Integer, Integer>>> toastLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> isGoogleUserDataUploadedLiveData = new MutableLiveData<>();


    public LoginViewModel(Application app) {
        this.app = app;
    }

    public void validatePassword(String password) {
        this.password = password;
        if (password.trim().length()<6)
            passwordErrorLiveData.setValue(new Event<>(app.getString(R.string.password_hint_error)));
        else
            passwordErrorLiveData.setValue(new Event<>(""));
    }

    public void validateEmail(String email) {
        this.email=email;
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            emailErrorLiveData.setValue(new Event<>(""));
        else
            emailErrorLiveData.setValue(new Event<>(app.getString(R.string.email_hint_error)));
        }


    void authenticateUser() {

        if (email.trim().isEmpty()||password.trim().isEmpty()) {
            validateEmail(email);
            validatePassword(password);
            return;
        }

        //show fake progressbar to simulate loading
        isLoadingLiveData.setValue(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    isLoadingLiveData.setValue(false);
                    navigateToMainLiveData.setValue(new Event<>(true));
                })
                .addOnFailureListener(e -> {
                    // If sign in fails, display a message to the user.
                    isLoadingLiveData.setValue(false);
                    toastLiveData.setValue(new Event<>(new Pair<>(R.string.credentials_error, 1)));
                });
    }


    LiveData<Event<String>> getPasswordError() {
        return passwordErrorLiveData;
    }

    LiveData<Event<String>> getEmailError() {
        return emailErrorLiveData;
    }

    LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    LiveData<Event<Boolean>> getNavigateToMainLiveData() {
        return navigateToMainLiveData;
    }

    LiveData<Event<Pair<Integer, Integer>>> getToastLiveData() {
        return toastLiveData;
    }

    public LiveData<Boolean> getIsGoogleUserDataUploadedLiveData() {
        return isGoogleUserDataUploadedLiveData;
    }

    public void observeNetworkChanges() {
        //todo
       /* IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(app);
        app.registerReceiver(receiver, filter);
        bl = receiver.is_connected();
        Log.d("Boolean ", bl.toString());*/
    }


    public void unregisterNetworkReceiver() {
        //todo
     /*   try {
            app.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.getStackTrace();
        }*/
    }


    public void handleGoogleSignIn(Intent data) {
        isLoadingLiveData.setValue(true);
        Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully
            addGoogleUserToDB(account);
        } catch (ApiException e) {
            //login with google failed, ask user to login with email and password
            toastLiveData.setValue(new Event<>(new Pair<>(R.string.use_gym_master_account, Toast.LENGTH_LONG)));
            isLoadingLiveData.setValue(false);
        }
    }


    void addGoogleUserToDB(final GoogleSignInAccount account) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        final User newUser = new User(account.getId(), account.getDisplayName(), account.getEmail(),
                account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);

        Log.i(TAG, "addGoogleUserToDB: " + account.getEmail());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (account.getId() != null && !dataSnapshot.hasChild(account.getId())) {
                    myRef.child(account.getId()).setValue(newUser).addOnSuccessListener(aVoid -> isGoogleUserDataUploadedLiveData.setValue(true));
                } else {
                    isGoogleUserDataUploadedLiveData.setValue(true);
                    isLoadingLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isGoogleUserDataUploadedLiveData.setValue(false);
                isLoadingLiveData.setValue(false);
            }
        });

    }

}


