package com.example.myapplication;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    ViewPager viewPager;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewPager=findViewById(R.id.view_pager);
        signUp=findViewById(R.id.sign_up);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout =  findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

      viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int i, float v, int i1) {

          }

          @Override
          public void onPageSelected(int i) {
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

                      break;
              }

          }

          @Override
          public void onPageScrollStateChanged(int i) {

          }
      });
    }



}





