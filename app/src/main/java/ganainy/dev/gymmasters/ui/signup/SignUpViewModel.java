package ganainy.dev.gymmasters.ui.signup;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.SharedPrefUtils;

import static ganainy.dev.gymmasters.utils.SharedPrefUtils.isFirstShowingOfSignUp;

public class SignUpViewModel extends ViewModel {

    private static final String TAG = "SignUpViewModel";
    private Application app;
    private MutableLiveData<Boolean> isFirstShowingOfSignUpLiveData = new MutableLiveData<>();
    private MutableLiveData<String> userNameErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> emailErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> passwordErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> navigateToMainLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Pair<Integer, Integer>>> toastLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> isUserDataUploadedLiveData = new MutableLiveData<>();


    MutableLiveData<Boolean> isGoogleUserDataUploadedLiveData = new MutableLiveData<>();

    MediatorLiveData<Boolean> isUserDataUploadedMediatorLiveData = new MediatorLiveData<>();


    public SignUpViewModel(final Application app) {
        this.app = app;

        isUserDataUploadedMediatorLiveData.addSource(isUserDataUploadedLiveData, isUserDataUploaded -> {
            if (isUserDataUploaded) {
                navigateToMainLiveData.setValue(new Event<>(true));
            } else {
                isLoadingLiveData.setValue(false);
                toastLiveData.setValue(new Event<>(new Pair<>(R.string.error_signing_up, 1)));
            }
        });


    }

    LiveData<Boolean> isFirstSignUpShowing() {
        isFirstShowingOfSignUpLiveData.setValue(SharedPrefUtils.getBoolean(app, isFirstShowingOfSignUp));
        return isFirstShowingOfSignUpLiveData;
    }

    public void updateIsFirstSignUpShowing() {
        SharedPrefUtils.putBoolean(app, false, isFirstShowingOfSignUp);
    }

    public void validateUserData(String userName, String email, String password, Uri imageUri) {

        if (userName.isEmpty())
            userNameErrorLiveData.setValue(app.getString(R.string.username_hint_error));
        if (email.isEmpty())
            emailErrorLiveData.setValue(app.getString(R.string.email_hint_error));
        if (password.isEmpty())
            passwordErrorLiveData.setValue(app.getString(R.string.password_hint_error));

        if (!userName.isEmpty() && !email.isEmpty() && !password.isEmpty())
            auth(userName, email, password, imageUri);
    }


    private void auth(final String userName, final String email, final String password, final Uri imageUri) {
        isLoadingLiveData.setValue(true);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (mAuth.getCurrentUser() != null) {
                        // createUser success, save user data in db and update ui
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserInfoInRealtimeDb(userId, imageUri, userName, email);
                    } else {
                        //  sign in fails, display a message to the user.
                        isLoadingLiveData.setValue(false);
                        toastLiveData.setValue(new Event<>(new Pair<>(R.string.signup_failed, 0)));
                    }
                })
                .addOnFailureListener(e -> {
                    //  sign in fails, display a message to the user.
                    isLoadingLiveData.setValue(false);
                    toastLiveData.setValue(new Event<>(new Pair<>(R.string.signup_failed, 0)));
                });

    }

    private void saveUserInfoInRealtimeDb(final String uid, Uri imageUri, String userName, String email) {

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        if (imageUri != null) {
            //uploadImage to firebase storage then upload data to realtime db
            final User newUser = new User(uid, userName, email, imageUri.getLastPathSegment());

            final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());
            imagesRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> myRef.child(uid).setValue(newUser).addOnSuccessListener(aVoid -> {
                Log.i(TAG, "onSuccess: ");
                isUserDataUploadedLiveData.setValue(true);
            }).addOnFailureListener(e -> {
                Log.i(TAG, "onFailure: " + e.getMessage());
                isUserDataUploadedLiveData.setValue(false);
            })).addOnFailureListener(e -> isUserDataUploadedLiveData.setValue(false));


        } else {
            //user didn't select image only upload data to realtime db
            User newUser = new User(uid, userName, email, null);
            myRef.child(uid).setValue(newUser).addOnSuccessListener(aVoid -> {
                Log.i(TAG, "onSuccess: ");
                isUserDataUploadedLiveData.setValue(true);
            }).addOnFailureListener(e -> {
                Log.i(TAG, "onFailure: " + e.getMessage());
                isUserDataUploadedLiveData.setValue(false);
            });
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isGoogleUserDataUploadedLiveData.setValue(false);
            }
        });

    }


    public LiveData<Boolean> getIsGoogleUserDataUploadedLiveData() {
        return isGoogleUserDataUploadedLiveData;
    }

    LiveData<String> getUsernameError() {
        return userNameErrorLiveData;
    }

    LiveData<String> getPasswordError() {
        return passwordErrorLiveData;
    }

    LiveData<String> getEmailError() {
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

    public void handleGoogleSignIn(Intent data) {
        Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully
            addGoogleUserToDB(account);
        } catch (ApiException e) {
            //login with google failed, ask user to login with email and password
            toastLiveData.setValue(new Event<>(new Pair<>(R.string.use_gym_master_account, Toast.LENGTH_LONG)));
        }
    }


}
