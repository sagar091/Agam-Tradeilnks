package com.example.sagar.myapplication.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sagar.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

                /*SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                if (preferences.contains("isUserLogin")) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    //Functions.fireIntent(SplashActivity.this, LoginActivity.class);
                }
                finish();*/
            }
        }.start();
    }

}
