package com.example.myapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.MyConstant;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private TextInputEditText emailEditText, passwordEditText;
    private Button login;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.loginButton);
        signup=findViewById(R.id.signupTextView);



        //open signup activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
               checkEmailAndPassword();
            }
        });


    }



    private void checkEmailAndPassword() {

        emailEditText =findViewById(R.id.emailEditText);
        passwordEditText =findViewById(R.id.passwordEditText);
        String fillhere = getResources().getString((R.string.fillhere_signuplogin_error));
        if (emailEditText.getText().toString().trim().isEmpty() || emailEditText.getText().toString().trim().equals(" "))
            emailEditText.setError(fillhere);
        else if (passwordEditText.getText().toString().trim().isEmpty() || passwordEditText.getText().toString().trim().equals(" "))
            passwordEditText.setError(fillhere);
        else
            loginToFirebase();
    }

    private void loginToFirebase() {
        //show fake progressbar to simulate loading
        final ConstraintLayout constraintLayout=findViewById(R.id.constraint);
        constraintLayout.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            constraintLayout.setVisibility(View.GONE);
                            //set loggedInUserId value which will be used later
                            MyConstant.loggedInUserId = mAuth.getUid();
                            FancyToast.makeText(LoginActivity.this,"Login successful.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            constraintLayout.setVisibility(View.GONE);
                            if (!haveNetworkConnection()) {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                FancyToast.makeText(LoginActivity.this, "Login failed, Check network connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                FancyToast.makeText(LoginActivity.this, "Login failed, Check you email and pasword.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }

                        }

                    }
                });


    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
