package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.UserProfile;
import com.rey.material.widget.Button;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private ImageView imgCart;
    private ProgressDialog pd;
    UserProfile userProfile;
    Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Check-In");
        setSupportActionBar(toolbar);
        toolbar.setPadding(40, 0, 0, 0);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        btnSkip = (Button)findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSkip:
                Intent intent = new Intent(CheckInActivity.this, MarketingDrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
    }
}
