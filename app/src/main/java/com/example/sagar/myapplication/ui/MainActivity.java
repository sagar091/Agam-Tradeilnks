package com.example.sagar.myapplication.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.Company;
import com.example.sagar.myapplication.model.CompanyData;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgAccount;
    private EditText edtCompany, edtModel;
    private Button btnGo;
    List<Company> companyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new GetCompany().execute();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgAccount = (ImageView) findViewById(R.id.imgAccount);

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.fireIntent(MainActivity.this, LoginActivity.class);
            }
        });

        edtCompany = (EditText) findViewById(R.id.edtCompany);
        edtModel = (EditText) findViewById(R.id.edtModel);
        btnGo = (com.rey.material.widget.Button) findViewById(R.id.btnGo);
    }

    private class GetCompany extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "subcat_list");
            JSONArray data;
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();

                CompanyData companyData = new GsonBuilder().create().fromJson(obj.toString(), CompanyData.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
