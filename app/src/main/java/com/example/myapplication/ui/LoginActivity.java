package com.example.myapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.utils.MyConstant;
import com.example.myapplication.utils.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public NetworkChangeReceiver receiver;
    Boolean bl = true;
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
        passwordEditText = findViewById(R.id.passwordEditText);
        checkInternet();

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
                //Hide keyboard and login
                login();
            }
        });


        /**after pressing done on keyboard after writing password it will login*/
        loginWithDone();
    }

    private void login() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        checkEmailAndPassword();
    }

    private void loginWithDone() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /* Write your logic here that will be executed when user taps next button */
                    login();

                    handled = true;
                }
                return handled;
            }
        });

    }


    private void checkEmailAndPassword() {

        emailEditText =findViewById(R.id.emailEditText);
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
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                FancyToast.makeText(LoginActivity.this, "Login failed, Check network connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                        }

                    }
                });


    }


    public void checkInternet() {
        //todo add this needed activities+unreigster on onpause
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(this);
        registerReceiver(receiver, filter);
        bl = receiver.is_connected();
        Log.d("Boolean ", bl.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
    }

}
