package com.example.sagar.myapplication.retailer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
            SettingDialog dialog = new SettingDialog(this);
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
