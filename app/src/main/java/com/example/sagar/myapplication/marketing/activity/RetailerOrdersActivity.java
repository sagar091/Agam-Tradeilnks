package com.example.sagar.myapplication.marketing.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.UserProfile;

import org.json.JSONObject;

import java.util.HashMap;

public class RetailerOrdersActivity extends AppCompatActivity {

    private String selectRetailerId, selectRetailerName;
    private TextView noData;
    String orderError;
    private Toolbar toolbar;
    private ImageView imgCart;
    View parentView;
    private String userId;
    ComplexPreferences complexPreferences;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_orders);

        selectRetailerId = getIntent().getStringExtra("selectRetailerId");
        selectRetailerName = getIntent().getStringExtra("selectRetailerName");

        init();

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        new GetOrders().execute();
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        noData = (TextView) findViewById(R.id.noData);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Orders");
        toolbar.setSubtitle("Retailer: " + selectRetailerName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(RetailerOrdersActivity.this, CartActivity.class);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class GetOrders extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(RetailerOrdersActivity.this, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "retailor_order");
            map.put("user_id", userId);
            map.put("retailor_id", selectRetailerId);
            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                Log.e("order_response", obj.toString());
                orderError = obj.getString("error");
                if (orderError.equals("0")) {

                }
            } catch (Exception e) {
                Functions.snack(parentView, e.getMessage());
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
