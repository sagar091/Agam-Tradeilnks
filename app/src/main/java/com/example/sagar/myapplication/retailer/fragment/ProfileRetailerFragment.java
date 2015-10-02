package com.example.sagar.myapplication.retailer.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
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

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.City;
import com.example.sagar.myapplication.model.CityModel;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.RetailerProfileModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class ProfileRetailerFragment extends Fragment {

    View parentView;
    private ComplexPreferences complexPreferences;
    String retailerId;
    private City city;
    EditText edtOutlet, edtMobile, edtMobile2, edtBirthDate, edtEmail, edtUsername, edtRetailer, edtRegDate, edtPassword, edtRePassword, edtPAN, edtTin,
            edtArea, edtAddress1, edtAddress2, edtCity, edtState, edtCountry, edtBrand;
    private String strOutlet, strMobile, strMobile2, strBirthDate, strEmail, strUsername, strRetailer, strPassword, strPAN, strTin, strProfile, strArea,
            strAddress1, strAddress2, strCity, strState, strCountry;
    private ImageView showPassword, showPassword2;
    Button btnUpdate;
    String selectCity, selectCompanyId;
    int cityError, profileError;
    ProgressDialog pd, pd1;
    boolean show = false, show2 = false;
    CompanyData companyData;
    RetailerProfileModel profileModel;

    public static ProfileRetailerFragment newInstance(String param1, String param2) {
        ProfileRetailerFragment fragment = new ProfileRetailerFragment();

        return fragment;
    }

    public ProfileRetailerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_profile_retailer, container, false);

        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        UserProfile userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        retailerId = userProfile.user_id;

        companyData = new CompanyData();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        companyData = complexPreferences.getObject("mobile_companies", CompanyData.class);

        init(parentView);

        city = new City();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        city = complexPreferences.getObject("city_list", City.class);

        if (city == null) {
            new LoadCity().execute();
        }

        edtBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (companyData.company.size() > 0) {
                    setCompanyDialog();
                } else {
                    Snackbar.make(parentView, "No data for company", Snackbar.LENGTH_SHORT).show();
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

        showPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show2) {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show2 = false;
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show2 = true;
                }
            }
        });

        profileModel = new RetailerProfileModel();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        profileModel = complexPreferences.getObject("current-retailer", RetailerProfileModel.class);
        if (profileModel == null) {
            new GetProfileData().execute();
        } else {
            setProfile();
        }

        return parentView;
    }

    private void setCompanyDialog() {

        ArrayList<String> cats = new ArrayList<String>();
        for (int i = 0; i < companyData.company.size(); i++) {
            cats.add(companyData.company.get(i).cat_name);
        }

        String[] stringItems = new String[cats.size()];
        stringItems = cats.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, parentView);
        dialog.isTitleShow(true).show();
        dialog.title("Select Prefered Company").titleTextSize_SP(20);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCompanyId = companyData.company.get(position).cat_id;
                edtBrand.setText(companyData.company.get(position).cat_name);
                dialog.dismiss();
            }
        });
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

    private void init(View parentView) {
        ((RetailerDrawerActivity) getActivity()).setTitle("Profile");
        ((RetailerDrawerActivity) getActivity()).setSubtitle("no");

        edtBrand = (EditText) parentView.findViewById(R.id.edtBrand);
        edtRePassword = (EditText) parentView.findViewById(R.id.edtRePassword);
        edtRegDate = (EditText) parentView.findViewById(R.id.edtRegDate);
        showPassword = (ImageView) parentView.findViewById(R.id.showPassword);
        showPassword2 = (ImageView) parentView.findViewById(R.id.showPassword2);
        btnUpdate = (Button) parentView.findViewById(R.id.btnUpdate);
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

    private class GetProfileData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd1 = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "edit_profile");
            map.put("retailor_id", "442");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("profile_response", obj.toString());

                profileError = obj.getInt("error");
                if (profileError == 0) {
                    profileModel = new GsonBuilder().create().fromJson(obj.toString(), RetailerProfileModel.class);
                    complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                    complexPreferences.putObject("current-retailer", profileModel);
                    complexPreferences.commit();
                }

            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd1.dismiss();
            if (profileError == 0) {
                setProfile();
            }
        }
    }

    private void setProfile() {
        edtOutlet.setText(profileModel.shop_no);
        edtMobile.setText(profileModel.mo_no);
        edtMobile2.setText(profileModel.mo_no2);
        edtBirthDate.setText(profileModel.bdate);
        edtEmail.setText(profileModel.user_email);
        edtUsername.setText(profileModel.user_login);
        edtRetailer.setText(profileModel.display_name);
        edtRegDate.setText(profileModel.registred_date);
        edtPAN.setText(profileModel.pan);
        edtTin.setText(profileModel.tin);
        edtArea.setText(profileModel.area);
        edtAddress1.setText(profileModel.address);
        edtAddress2.setText(profileModel.add_1);
        edtCity.setText(profileModel.city);
        edtState.setText(profileModel.state);
        edtCountry.setText(profileModel.country);
        edtBrand.setText(profileModel.prefered_brand);

    }
}
