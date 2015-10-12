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
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.example.sagar.myapplication.model.City;
import com.example.sagar.myapplication.model.CityModel;
import com.example.sagar.myapplication.model.NearByUsers;
import com.example.sagar.myapplication.model.RetailerData;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.model.Users;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener, OfflineRetailerDialog.onSelectRetailerListner {

    private Toolbar toolbar;
    ListView usersListView;
    private ImageView imgCart;
    private ProgressDialog pd;
    View parentView, include;
    UserProfile userProfile;
    Button btnSkip, btnOffline, btnCheckIn, btnNewRetailer;
    private String offlineError;
    RetailerData retailerData;
    LocationFinder finder;
    private ComplexPreferences complexPreferences;
    double longitude = 0.0, latitude = 0.0;
    int error;
    NearByUsers nearByUsers;
    ListViewAdapter adapter;
    ScrollView newRetailerLayout;

    // ADD RETAILER SECTION
    EditText edtOutlet, edtMobile, edtMobile2, edtBirthDate, edtEmail, edtUsername, edtRetailer, edtPassword, edtPAN, edtTin, edtProfile, edtArea,
            edtAddress1, edtAddress2, edtCity, edtState, edtCountry;
    private String strOutlet, strMobile, strMobile2, strBirthDate, strEmail, strUsername, strRetailer, strPassword, strPAN, strTin, strProfile, strArea,
            strAddress1, strAddress2, strCity, strState, strCountry;
    private ImageView showPassword;
    boolean show = false;
    RadioGroup radioGroup;
    ProgressDialog pd1;
    LinearLayout linearAddress;
    int radioCheckedId = 0;
    int cityError;
    private City city;
    Button btnAdd;
    String selectCity, msg, userId, addError, newRetailerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        init();

        // getLocationStatus();
        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioCheckedId = checkedId;
                if (radioCheckedId == R.id.radioCurrent) {
                    linearAddress.setVisibility(View.GONE);
                } else if (radioCheckedId == R.id.radioAddress) {
                    linearAddress.setVisibility(View.VISIBLE);
                }
            }
        });

        new GetRetailers().execute();

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLocationStatus()) {
                    new GeNearByUsers().execute();
                }
            }
        });

        btnNewRetailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersListView.setVisibility(View.GONE);
                include.setVisibility(View.VISIBLE);
            }
        });

        // ADD RETAILER SECTION
        city = new City();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        city = complexPreferences.getObject("city_list", City.class);

        if (city == null) {
            new LoadCity().execute();
        }
        edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                edtBirthDate.setText(new StringBuilder().append(dayOfMonth).append("-")
                                        .append(monthOfYear + 1).append("-").append(year));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Select Birthdate");
            }
        });

        edtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city.cityModels.size() > 0) {
                    setCityDialog();
                }
            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show) {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show = false;
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show = true;
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    new AddRetailer().execute();
                }
            }
        });
    }

    private boolean checkValidation() {

        boolean valid = true;

        if (Functions.getLength(edtOutlet) == 0) {
            Functions.showSnack(parentView, "Enter Outlet Name");
            valid = false;

        } else if (Functions.getLength(edtMobile) != 10) {
            Functions.showSnack(parentView, "Enter mobile number of 10 digits");
            valid = false;

        } else if (Functions.getLength(edtMobile2) != 10) {
            Functions.showSnack(parentView, "Enter mobile number-2 of 10 digits");
            valid = false;

        } else if (Functions.getLength(edtBirthDate) == 0) {
            Functions.showSnack(parentView, "Select Birthdate");
            valid = false;

        } else if (Functions.getLength(edtEmail) == 0 || !Functions.emailValidation(edtEmail.getText().toString())) {
            Functions.showSnack(parentView, "Enter valid Email-id");
            valid = false;

        } else if (Functions.getLength(edtUsername) == 0) {
            Functions.showSnack(parentView, "Enter User Name");
            valid = false;

        } else if (Functions.getLength(edtRetailer) == 0) {
            Functions.showSnack(parentView, "Enter Retailer Name");
            valid = false;

        } else if (Functions.getLength(edtPassword) < 6) {
            Functions.showSnack(parentView, "Enter password minimum of 6 characters");
            valid = false;

        } else if (radioCheckedId == R.id.radioCurrent) {
            if (latitude == 0.0 || longitude == 0.0) {
                getLocationStatus();
                valid = false;
            }

        } else {
            if (Functions.getLength(edtArea) == 0) {
                Functions.showSnack(parentView, "Enter Area");
                valid = false;

            } else if (Functions.getLength(edtAddress1) == 0) {
                Functions.showSnack(parentView, "Enter Address Line 1");
                valid = false;

            } else if (Functions.getLength(edtAddress2) == 0) {
                Functions.showSnack(parentView, "Enter Address Line 2");
                valid = false;

            } else if (Functions.getLength(edtCity) == 0) {
                Functions.showSnack(parentView, "Select City");
                valid = false;

            } else if (Functions.getLength(edtState) == 0) {
                Functions.showSnack(parentView, "Enter State");
                valid = false;

            } else if (Functions.getLength(edtCountry) == 0) {
                Functions.showSnack(parentView, "Enter Country");
                valid = false;
            }

        }

        if (valid) {
            strOutlet = edtOutlet.getText().toString().trim();
            strMobile = edtMobile.getText().toString().trim();
            strMobile2 = edtMobile2.getText().toString().trim();
            strBirthDate = edtBirthDate.getText().toString().trim();
            strEmail = edtEmail.getText().toString().trim();
            strUsername = edtUsername.getText().toString().trim();
            strRetailer = edtRetailer.getText().toString().trim();
            strPassword = edtPassword.getText().toString().trim();
            strPAN = edtPAN.getText().toString().trim();
            strTin = edtTin.getText().toString().trim();

            if (radioCheckedId == R.id.radioCurrent) {
                strArea = "";
                strAddress1 = "";
                strAddress2 = "";
                strCountry = "";
                strCity = "";
                strState = "";

            } else {
                strArea = edtArea.getText().toString().trim();
                strAddress1 = edtAddress1.getText().toString().trim();
                strAddress2 = edtAddress2.getText().toString().trim();
                strCity = edtCity.getText().toString().trim();
                strState = edtState.getText().toString().trim();
                strCountry = edtCountry.getText().toString().trim();
            }
        }


        return valid;
    }

    private class AddRetailer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(CheckInActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "user_registration");
            map.put("user_id", userId);
            map.put("shop_no", strOutlet);
            map.put("mo_no", strMobile);
            map.put("mo_no2", strMobile2);
            map.put("bdate", strBirthDate);
            map.put("email", strEmail);
            map.put("username", strUsername);
            map.put("r_name", strRetailer);
            map.put("password", strPassword);
            map.put("pan", strPAN);
            map.put("tin", strTin);
            map.put("pro_pic", "null");
            map.put("area", strArea);
            map.put("address", strAddress1);
            map.put("add_1", strAddress2);
            map.put("city", strCity);
            map.put("state", strState);
            map.put("country", strCountry);

            if (radioCheckedId == R.id.radioCurrent) {
                map.put("lat", latitude + "");
                map.put("long", longitude + "");
            } else {
                map.put("lat", "0.0");
                map.put("long", "0.0");
            }
            Log.e("req", map.toString());
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("add_retailer_response", obj.toString());
                JSONObject json2 = obj.getJSONObject("status");
                msg = json2.getString("msg");
                addError = json2.getString("error");
                if (addError.equals("0")) {
                    newRetailerId = json2.getString("retailor_id");
                }
            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (addError.equals("0")) {
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("offline", newRetailerId);
                editor.commit();

                Functions.showSnack(parentView, msg + "and Check-In successfully.");

                Intent intent = new Intent(CheckInActivity.this, MarketingDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                Functions.showSnack(parentView, msg);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getLocationStatus();
    }

    private void setCityDialog() {
        final ArrayList<String> cities = new ArrayList<>();
        for (CityModel model : city.cityModels) {
            if (model.city_state.equals("Gujarat")) {
                cities.add(model.city_name);
            }
        }

        String[] stringItems = new String[cities.size()];
        stringItems = cities.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, include);
        dialog.isTitleShow(true).show();
        dialog.title("Select City").titleTextSize_SP(20);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity = cities.get(position);
                edtCity.setText(selectCity);
                Log.e("selectCity", selectCity);
                dialog.dismiss();
            }
        });
    }

    private class LoadCity extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd1 = ProgressDialog.show(CheckInActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_city_state");

            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("city_response", obj.toString());

                cityError = obj.getInt("error");

                if (cityError == 0) {
                    city = new GsonBuilder().create().fromJson(obj.toString(), City.class);
                    complexPreferences = ComplexPreferences.getComplexPreferences(CheckInActivity.this, "user_pref", 0);
                    complexPreferences.putObject("city_list", city);
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
            pd1.dismiss();

        }
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
        include = findViewById(R.id.include);
        findViewById(include);

        newRetailerLayout = (ScrollView) findViewById(R.id.newRetailerLayout);
        btnNewRetailer = (Button) findViewById(R.id.btnNewRetailer);
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

    private void findViewById(View include) {
        showPassword = (ImageView) include.findViewById(R.id.showPassword);
        radioGroup = (RadioGroup) include.findViewById(R.id.radioGroup);
        linearAddress = (LinearLayout) include.findViewById(R.id.linearAddress);
        btnAdd = (Button) include.findViewById(R.id.btnAdd);
        btnAdd.setText("Add Retailer and Check In");
        edtCity = (EditText) include.findViewById(R.id.edtCity);
        edtOutlet = (EditText) include.findViewById(R.id.edtOutlet);
        edtMobile = (EditText) include.findViewById(R.id.edtMobile);
        edtMobile2 = (EditText) include.findViewById(R.id.edtMobile2);
        edtBirthDate = (EditText) include.findViewById(R.id.edtBirthDate);
        edtEmail = (EditText) include.findViewById(R.id.edtEmail);
        edtUsername = (EditText) include.findViewById(R.id.edtUsername);
        edtRetailer = (EditText) include.findViewById(R.id.edtRetailer);
        edtPassword = (EditText) include.findViewById(R.id.edtPassword);
        edtPAN = (EditText) include.findViewById(R.id.edtPAN);
        edtPAN.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edtTin = (EditText) include.findViewById(R.id.edtTin);
        edtProfile = (EditText) include.findViewById(R.id.edtProfile);
        edtArea = (EditText) include.findViewById(R.id.edtArea);
        edtAddress1 = (EditText) include.findViewById(R.id.edtAddress1);
        edtAddress2 = (EditText) include.findViewById(R.id.edtAddress2);
        edtState = (EditText) include.findViewById(R.id.edtState);
        edtCountry = (EditText) include.findViewById(R.id.edtCountry);
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
            map.put("lat", latitude + "");
            map.put("long", longitude + "");
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
                usersListView.setVisibility(View.VISIBLE);
                include.setVisibility(View.GONE);
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
                "We cannot fetch retailers nearby your location. Please add retailer manually or choose for offline retailers.")
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
