package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.SettingDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.helper.LocationFinder;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.City;
import com.example.sagar.myapplication.model.CityModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewRetailerFragment extends Fragment {

    View parentView;
    EditText edtOutlet, edtMobile, edtMobile2, edtBirthDate, edtEmail, edtUsername, edtRetailer, edtPassword, edtPAN, edtTin, edtProfile, edtArea,
            edtAddress1, edtAddress2, edtCity, edtState, edtCountry;
    private String strOutlet, strMobile, strMobile2, strBirthDate, strEmail, strUsername, strRetailer, strPassword, strPAN, strTin, strProfile, strArea,
            strAddress1, strAddress2, strCity, strState, strCountry;
    private ImageView showPassword;
    boolean show = false;
    RadioGroup radioGroup;
    ProgressDialog pd;
    LinearLayout linearAddress;
    int radioCheckedId = 0;
    int cityError;
    private City city;
    private ComplexPreferences complexPreferences;
    LocationFinder finder;
    Button btnAdd;
    double longitude = 0.0, latitude = 0.0;
    String selectCity, userId, msg, addError;

    public static AddNewRetailerFragment newInstance() {
        AddNewRetailerFragment fragment = new AddNewRetailerFragment();
        return fragment;
    }

    public AddNewRetailerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_add_new_retailer, container, false);

        init(parentView);

        // getLocationStatus();
        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        radioCheckedId = R.id.radioCurrent;

        city = new City();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        city = complexPreferences.getObject("city_list", City.class);

        if (city == null) {
            new LoadCity().execute();
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    new AddRetailer().execute();
                }
            }
        });

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
                dpd.show(getActivity().getFragmentManager(), "Select Birthdate");
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

        return parentView;
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

    private void setCityDialog() {
        final ArrayList<String> cities = new ArrayList<>();
        for (CityModel model : city.cityModels) {
            if (model.city_state.equals("Gujarat")) {
                cities.add(model.city_name);
            }
        }

        String[] stringItems = new String[cities.size()];
        stringItems = cities.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, parentView);
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

    private void getLocationStatus() {
        finder = new LocationFinder(getActivity());
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();

        } else {
            final SettingDialog dialog = new SettingDialog(getActivity(), "GPS is not enabled. Please go to settings menu to enable GPS or mention manually your address.", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            dialog.setOnExitListener(new SettingDialog.OnExitListener() {
                @Override
                public void exit() {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void init(View parentView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Add New Retailer");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");

        findViewById(parentView);

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
    }

    private void findViewById(View parentView) {
        showPassword = (ImageView) parentView.findViewById(R.id.showPassword);
        radioGroup = (RadioGroup) parentView.findViewById(R.id.radioGroup);
        linearAddress = (LinearLayout) parentView.findViewById(R.id.linearAddress);
        btnAdd = (Button) parentView.findViewById(R.id.btnAdd);
        edtCity = (EditText) parentView.findViewById(R.id.edtCity);
        edtOutlet = (EditText) parentView.findViewById(R.id.edtOutlet);
        edtMobile = (EditText) parentView.findViewById(R.id.edtMobile);
        edtMobile2 = (EditText) parentView.findViewById(R.id.edtMobile2);
        edtBirthDate = (EditText) parentView.findViewById(R.id.edtBirthDate);
        edtEmail = (EditText) parentView.findViewById(R.id.edtEmail);
        edtUsername = (EditText) parentView.findViewById(R.id.edtUsername);
        edtRetailer = (EditText) parentView.findViewById(R.id.edtRetailer);
        edtPassword = (EditText) parentView.findViewById(R.id.edtPassword);
        edtPAN = (EditText) parentView.findViewById(R.id.edtPAN);
        edtPAN.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edtTin = (EditText) parentView.findViewById(R.id.edtTin);
        edtProfile = (EditText) parentView.findViewById(R.id.edtProfile);
        edtArea = (EditText) parentView.findViewById(R.id.edtArea);
        edtAddress1 = (EditText) parentView.findViewById(R.id.edtAddress1);
        edtAddress2 = (EditText) parentView.findViewById(R.id.edtAddress2);
        edtState = (EditText) parentView.findViewById(R.id.edtState);
        edtCountry = (EditText) parentView.findViewById(R.id.edtCountry);
    }

    private class LoadCity extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
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
                    complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
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
            pd.dismiss();

        }
    }

    private class AddRetailer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
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
                Functions.showSnack(parentView, msg);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getLocationStatus();
    }
}
