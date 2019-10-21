package com.example.myapplication.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    private static final int PICK_IMAGE = 101;
    private static final int RC_SIGN_IN = 102;
    CircleImageView profileImage;
    private TextInputEditText userNameEditText, emailEditText, passwordEditText;
    private Uri imageUri;
    Button signUp;
    ProgressBar progressBar;
    FloatingActionButton addProfilePhoto;
    private FirebaseAuth mAuth;
    private String userName, email;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        profileImage = findViewById(R.id.profile_image);
        addProfilePhoto = findViewById(R.id.addProfilePhoto);
        signUp = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);

        selectPhoto();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserNameAndEmailAndPassword();
            }
        });


        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
    }

    private void checkUserNameAndEmailAndPassword() {

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        userNameEditText = findViewById(R.id.userNameEditText);
        userName = userNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String fillhere = getResources().getString((R.string.fillhere_signuplogin_error));
        if (userName.isEmpty() || userName.equals(" "))
            userNameEditText.setError(fillhere);
        else if (email.isEmpty() || email.equals(" "))
            emailEditText.setError(fillhere);
        else if (password.isEmpty() || password.equals(" "))
            passwordEditText.setError(fillhere);
        else
            auth(email, password);
    }

    private void auth(String email, String password) {
        final ConstraintLayout constraintLayout = findViewById(R.id.constraint);
        constraintLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            constraintLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            FancyToast.makeText(SignUpActivity.this, "Registration successful.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            if (imageUri != null)
                                intent.putExtra("imageUri", imageUri.toString());
                            startActivity(intent);
                            String uid = mAuth.getCurrentUser().getUid();
                            saveUserInfoInRealtimeDb(uid);
                            //save user data related to sending device to device notification later

                        } else {

                            // If sign in fails, display a message to the user.
                            constraintLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            FancyToast.makeText(SignUpActivity.this, task.getException() + "", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                        }

                        // ...
                    }
                });
    }


    private void saveUserInfoInRealtimeDb(String uid) {

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        User newUser;
        if (imageUri != null) {
            //uploadImage to firebase storage
            uploadProfilePic(imageUri);
            newUser = new User(uid, userName, email, imageUri.getLastPathSegment());
        } else {
            newUser = new User(uid, userName, email, "-1");
        }

        myRef.child(uid).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    private void uploadProfilePic(Uri imageUri) {
        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child("images/" + imageUri.getLastPathSegment());
        imagesRef.putFile(imageUri);
    }

    private void selectPhoto() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        addProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    public void onStart() {
        super.onStart();

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            //ask user to login
            Log.i(TAG, "updateUI: account is null");
            FancyToast.makeText(SignUpActivity.this, "Error signing in with google account ,\n please sign up with Gym master account instead.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {

            //user already added his google account
            /**live data to observe and return when user data is saved */
            addGoogleUserToDB(account).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        /**executes if account was added to db or not added if already existed*/
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        FancyToast.makeText(SignUpActivity.this, "Login Successful.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                        startActivity(intent);
                        finish();
                    } else {
                        /**onCancelled called when adding or checking if google account is in db*/
                        FancyToast.makeText(SignUpActivity.this, "Error signing in with google account ,\n please login with Gym master account instead.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }
                }
            });

        }
    }

    private LiveData<Boolean> addGoogleUserToDB(final GoogleSignInAccount account) {
        final MutableLiveData<Boolean> load = new MutableLiveData<>();
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        final User newUser;

        newUser = new User(account.getId(), account.getDisplayName(), account.getEmail(),
                account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);

        Log.i(TAG, "addGoogleUserToDB: " + account.getEmail());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(account.getId())) {
                    myRef.child(account.getId()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            load.setValue(true);
                        }
                    });
                } else {
                    load.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                load.setValue(false);
            }
        });

        return load;
    }

    @Override
    public void onClick(View view) {
        /**google sign in clicked*/
        if (view.getId() == R.id.sign_in_button) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);

        }
    }
}
