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

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int PICK_IMAGE = 101;
    CircleImageView profileImage;
    private TextInputEditText userNameEditText, emailEditText, passwordEditText;
    private Uri imageUri;
    Button signUp;
    //private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userName,email;
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
        userName = userNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
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
                            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                            if (imageUri!=null)
                            intent.putExtra("imageUri",imageUri.toString());
                            startActivity(intent);
                            String uid = mAuth.getCurrentUser().getUid();
                            saveUserInfoInRealtimeDb(uid);

                        } else {

                            // If sign in fails, display a message to the user.
                            constraintLayout.setVisibility(View.GONE);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            FancyToast.makeText(SignUpActivity.this,"Registration failed.",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();

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
        if (imageUri!=null)
        {
            //uploadImage to firebase storage
            uploadProfilePic(imageUri);
            newUser = new User(uid, userName, email, "-1", imageUri.getLastPathSegment());
        }
        else
        {
            newUser = new User(uid, userName, email, "-1", "-1");
        }

        myRef.child(uid).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });

    }

    private void  uploadProfilePic(Uri imageUri) {
        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();


        final StorageReference imagesRef = storageRef.child("images/"+imageUri.getLastPathSegment());
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

    }
}
