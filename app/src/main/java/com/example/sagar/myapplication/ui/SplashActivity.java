package com.example.sagar.myapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.marketing.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.RetailerDrawerActivity;

public class SplashActivity extends AppCompatActivity {

    private ComplexPreferences complexPreferences;
    private String userType;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                if (preferences.contains("isUserLogin")) {
                    complexPreferences = ComplexPreferences.getComplexPreferences(SplashActivity.this, "user_pref", 0);
                    UserProfile currentUser = new UserProfile();
                    currentUser = complexPreferences.getObject("current-user", UserProfile.class);
                    userType = currentUser.user_type;

                    if (userType.equals("1")) {
                        intent = new Intent(SplashActivity.this, MarketingDrawerActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, RetailerDrawerActivity.class);
                    }
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);

                }
                startActivity(intent);
                finish();
            }
        }.start();
    }

}
