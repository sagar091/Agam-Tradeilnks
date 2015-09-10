package com.example.sagar.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.ui.LoginActivity;
import com.rey.material.widget.Button;

public class MainActivityAnother extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgAccount;
    private EditText edtCompany, edtModel;
    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_another);

        init();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgAccount = (ImageView) findViewById(R.id.imgAccount);

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.fireIntent(MainActivityAnother.this, LoginActivity.class);
            }
        });

        edtCompany = (EditText) findViewById(R.id.edtCompany);
        edtModel = (EditText) findViewById(R.id.edtModel);
        btnGo = (Button) findViewById(R.id.btnGo);
    }

}
