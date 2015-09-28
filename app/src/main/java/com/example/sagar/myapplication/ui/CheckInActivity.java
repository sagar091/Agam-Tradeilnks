package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.OfflineRetailerDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.RetailerData;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener, OfflineRetailerDialog.onSelectRetailerListner {

    private Toolbar toolbar;
    private ImageView imgCart;
    private ProgressDialog pd;
    View parentView;
    UserProfile userProfile;
    Button btnSkip, btnOffline;
    private String offlineError;
    RetailerData retailerData;
    private ComplexPreferences complexPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        init();

        new GetRetailers().execute();
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Check-In");
        setSupportActionBar(toolbar);
        toolbar.setPadding(40, 0, 0, 0);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnOffline = (Button) findViewById(R.id.btnOffline);
        btnSkip.setOnClickListener(this);
        btnOffline.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSkip:
                Intent intent = new Intent(CheckInActivity.this, MarketingDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.btnOffline:
                RetailerData retailerData = new RetailerData();
                complexPreferences = ComplexPreferences.getComplexPreferences(CheckInActivity.this, "user_pref", 0);
                retailerData = complexPreferences.getObject("offline_retailers", RetailerData.class);

                if (retailerData.retailers.size() > 0) {

                    OfflineRetailerDialog dialog = new OfflineRetailerDialog(CheckInActivity.this, retailerData);
                    dialog.show();
                    dialog.setOnSelectRetailerListner(this);

                } else {
                    Snackbar.make(v, "No retailer", Snackbar.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    public void OnClickRetailer(String selectRetailerId, String selectRetailerName) {
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("offline", selectRetailerId);
        editor.commit();

        Functions.showSnack(parentView, "Check-In successfully.");

        Intent intent = new Intent(CheckInActivity.this, MarketingDrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private class GetRetailers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(CheckInActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "retailor_list");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("retailors_response", obj.toString());
                offlineError = obj.getString("error");
                Log.e("offlineError", offlineError);
                if (offlineError.equals("0")) {
                    retailerData = new GsonBuilder().create().fromJson(obj.toString(), RetailerData.class);
                    Log.e("data_offline", retailerData.retailers.size() + "--");

                    complexPreferences = ComplexPreferences.getComplexPreferences(CheckInActivity.this, "user_pref", 0);
                    complexPreferences.putObject("offline_retailers", retailerData);
                    complexPreferences.commit();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
        }
    }
}
