package com.example.sagar.myapplication.retailer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.SettingDialog;
import com.example.sagar.myapplication.helper.Functions;

public class RetailerDrawerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_drawer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.isConnecting(this)) {
            SettingDialog dialog = new SettingDialog(this, "You don't seem to have an active internet connection. Please check your internet connectivity and come again.", android.provider.Settings.ACTION_SETTINGS);
            dialog.setOnExitListener(new SettingDialog.OnExitListener() {
                @Override
                public void exit() {
                    finish();
                }
            });
            dialog.show();
        }
    }
}
