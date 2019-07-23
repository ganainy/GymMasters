package com.example.myapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int PICK_IMAGE = 101;
    CircleImageView profileImage;
    private TextInputEditText userNameEditText, emailEditText, passwordEditText;
    private Uri imageUri;
    Button signUp;
    private ProgressBar progressBar;
    //private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
   // private StorageReference mStorageRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        profileImage =findViewById(R.id.profile_image);
        signUp=findViewById(R.id.signupButton);
        
        selectPhoto();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserNameAndEmailAndPassword();
            }
        });


    }

    private void checkUserNameAndEmailAndPassword() {

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        userNameEditText=findViewById(R.id.userNameEditText);
        final String userName = userNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String fillhere = getResources().getString((R.string.fillhere_signuplogin_error));
        if (userName.isEmpty() || userName.equals(" "))
            userNameEditText.setError(fillhere);
        else if (email.isEmpty() || email.equals(" "))
            emailEditText.setError(fillhere);
        else if(password.isEmpty()||password.equals(" "))
            passwordEditText.setError(fillhere);
        else
        auth(email,password);
    }

    private void auth(String email,String password) {
        final ConstraintLayout constraintLayout=findViewById(R.id.constraint);
        constraintLayout.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            constraintLayout.setVisibility(View.GONE);
                            Log.d(TAG, "createUserWithEmail:success");
                            FancyToast.makeText(SignUpActivity.this,"Registration successful.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                         //   updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            constraintLayout.setVisibility(View.GONE);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            FancyToast.makeText(SignUpActivity.this,"Registration failed.",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }
}
