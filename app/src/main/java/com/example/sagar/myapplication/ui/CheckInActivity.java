package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.OfflineRetailerDialog;
import com.example.sagar.myapplication.customComponent.SettingDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.helper.LocationFinder;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.NearByUsers;
import com.example.sagar.myapplication.model.RetailerData;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.model.Users;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener, OfflineRetailerDialog.onSelectRetailerListner {

    private Toolbar toolbar;
    ListView usersListView;
    private ImageView imgCart;
    private ProgressDialog pd;
    View parentView;
    UserProfile userProfile;
    Button btnSkip, btnOffline, btnCheckIn;
    private String offlineError;
    RetailerData retailerData;
    LocationFinder finder;
    private ComplexPreferences complexPreferences;
    double longitude = 0.0, latitude = 0.0;
    int error;
    NearByUsers nearByUsers;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        init();

        new GetRetailers().execute();

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLocationStatus()) {
                    new GeNearByUsers().execute();
                }
            }
        });
    }

    private boolean getLocationStatus() {
        boolean getStatus = false;

        finder = new LocationFinder(this);
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();

            getStatus = true;

        } else {
            final SettingDialog dialog = new SettingDialog(this, "GPS is not enabled. Please go to settings menu to enable GPS or mention manually your address.", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            dialog.setOnExitListener(new SettingDialog.OnExitListener() {
                @Override
                public void exit() {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return getStatus;
    }

    private void init() {
        usersListView = (ListView) findViewById(R.id.usersListView);
        btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
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
                //Log.e("offlineError", offlineError);
                if (offlineError.equals("0")) {
                    retailerData = new GsonBuilder().create().fromJson(obj.toString(), RetailerData.class);
                    //Log.e("data_offline", retailerData.retailers.size() + "--");

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

    private class GeNearByUsers extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(CheckInActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_nearby");
            map.put("lat", "23.0386084");
            map.put("long", "72.513667");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("nearby_response", obj.toString());

                error = obj.getInt("error");
                if (error == 0) {
                    nearByUsers = new GsonBuilder().create().fromJson(obj.toString(), NearByUsers.class);
                }

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (error == 0) {
                Log.e("nearByUsers", nearByUsers.users.size() + "--");
                adapter = new ListViewAdapter(CheckInActivity.this, nearByUsers.users);
                usersListView.setAdapter(adapter);
            } else {
                noUsersAlert();
            }
        }
    }

    private void noUsersAlert() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "We cannot fetch users nearby you. Please add retailer manually details and then check in.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private class ListViewAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        List<Users> users;

        public ListViewAdapter(Context context, List<Users> users) {
            this.context = context;
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder mHolder;
            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.checkin_item,
                        parent, false);
                mHolder = new ViewHolder();
                mHolder.txtRetailerName = (TextView) convertView.findViewById(R.id.txtRetailerName);
                mHolder.btnHere = (Button) convertView.findViewById(R.id.btnHere);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.txtRetailerName.setText(users.get(position).user.Username);
            mHolder.btnHere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("offline", users.get(position).user.retailor_id);
                    editor.commit();

                    Functions.showSnack(parentView, "Check-In successfully.");

                    Intent intent = new Intent(CheckInActivity.this, MarketingDrawerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView txtRetailerName;
            Button btnHere;
        }
    }
}
