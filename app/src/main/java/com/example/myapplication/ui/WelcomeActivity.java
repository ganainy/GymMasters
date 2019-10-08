package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.fragments.ViewPagerAdapterWelcomeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    ViewPager viewPager;
    Button signUp;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        viewPager=findViewById(R.id.view_pager);
        signUp=findViewById(R.id.sign_up);
        login=findViewById(R.id.logInTextView);

        //view pager and tab layout for swiping fragments
        viewPager.setAdapter(new ViewPagerAdapterWelcomeActivity(getSupportFragmentManager()));
        TabLayout tabLayout =  findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        showSignUpButton();


        //handle login click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            }
        });
        //handle signup click
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open signup activity
                startActivity(new Intent(WelcomeActivity.this,SignUpActivity.class));

            }
        });


    }

    private void showSignUpButton() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //show sign up button on third fragment only + show simple animation
                switch (i)
                {

                    case 0:

                        signUp.setVisibility(View.GONE);

                        break;
                    case 1:
                        signUp.setVisibility(View.GONE);

                        break;
                    case 2:
                        signUp.setVisibility(View.VISIBLE);
                        final Animation animTranslate = AnimationUtils.loadAnimation(WelcomeActivity.this,                          R.anim.anim_translate);
                        signUp.startAnimation(animTranslate);

                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in and open app with asking him to login again
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
        }
    }

}





