package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.SettingDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.helper.LocationFinder;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.City;
import com.example.sagar.myapplication.model.CityModel;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.RetailerDrawerActivity;
import com.example.sagar.myapplication.ui.CheckInActivity;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddNewRetailerFragment extends Fragment {

    View parentView;
    EditText edtCity;
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
    String selectCity;

    public static AddNewRetailerFragment newInstance(String param1, String param2) {
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

        radioCheckedId = R.id.radioCurrent;

        city = new City();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        city = complexPreferences.getObject("city_list", City.class);

        if (city == null) {
            Log.e("city", "null");
            new LoadCity().execute();
        } else {
            Log.e("city", "data");
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        return parentView;
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
                new CountDownTimer(900, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        finder = new LocationFinder(getActivity());
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();

            Toast.makeText(getActivity(), "location-" + latitude + "--" + longitude, Toast.LENGTH_LONG).show();

        } else {
            final SettingDialog dialog = new SettingDialog(getActivity(), "GPS is not enabled. Do you want to go to settings menu?", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        radioGroup = (RadioGroup) parentView.findViewById(R.id.radioGroup);
        linearAddress = (LinearLayout) parentView.findViewById(R.id.linearAddress);
        btnAdd = (Button) parentView.findViewById(R.id.btnAdd);
        edtCity = (EditText) parentView.findViewById(R.id.edtCity);

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
}
