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
    String retailerId, password;
    private City city;
    EditText edtOutlet, edtMobile, edtMobile2, edtBirthDate, edtEmail, edtUsername, edtRetailer, edtRegDate, edtPassword, edtRePassword, edtPAN, edtTin,
            edtArea, edtAddress1, edtAddress2, edtCity, edtState, edtCountry, edtBrand;
    private String strOutlet, strMobile, strMobile2, strBirthDate, strEmail, strUsername, strRetailer, strPassword, strPAN, strTin, strProfile, strArea,
            strAddress1, strAddress2, strCity, strState, strCountry;
    private ImageView showPassword, showPassword2;
    Button btnUpdate;
    String selectCity, selectCompanyId;
    int cityError, profileError, updateError;
    ProgressDialog pd, pd1, pd2;
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
        password = userProfile.password;

        Log.e("retailerId", retailerId);
        companyData = new CompanyData();
        companyData = complexPreferences.getObject("mobile_companies", CompanyData.class);

        init(parentView);

        city = new City();
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
                openDatePicker();

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
                    edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show2 = false;
                } else {
                    edtRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show2 = true;
                }
            }
        });

        profileModel = new RetailerProfileModel();
        profileModel = complexPreferences.getObject("current-retailer", RetailerProfileModel.class);

        if (profileModel.username == null) {
            new GetProfileData().execute();
        } else {
            setProfile();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    new UpdateRetailer().execute();
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

        } else if (Functions.getLength(edtArea) == 0) {
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

        } else if (Functions.getLength(edtPassword) < 6) {
            Functions.showSnack(parentView, "Enter password minimum of 6 characters");
            valid = false;

        } else if (Functions.getLength(edtRePassword) < 6) {
            Functions.showSnack(parentView, "Enter password minimum of 6 characters");
            valid = false;

        } else if (!edtPassword.getText().toString().equals(edtRePassword.getText().toString())) {
            Functions.showSnack(parentView, "Password and Re-type password does not match.");
            valid = false;

        } else if (Functions.getLength(edtBrand) == 0 && edtBrand.getHint().toString().equals("Select Prefered Company")) {
            Functions.showSnack(parentView, "Select your prefered brand");
            valid = false;
        }

        if (valid) {

            profileModel = new RetailerProfileModel();
            profileModel.outlet = Functions.getText(edtOutlet);
            profileModel.error = "0";
            profileModel.mobile1 = Functions.getText(edtMobile);
            profileModel.mobile2 = Functions.getText(edtMobile2);
            profileModel.bdate = Functions.getText(edtBirthDate);
            profileModel.user_email = Functions.getText(edtEmail);
            profileModel.username = Functions.getText(edtUsername);
            profileModel.retailerName = Functions.getText(edtRetailer);
            profileModel.registred_date = Functions.getText(edtRegDate);
            profileModel.pan = Functions.getText(edtPAN);
            profileModel.tin = Functions.getText(edtTin);
            profileModel.area = Functions.getText(edtArea);
            profileModel.address1 = Functions.getText(edtAddress1);
            profileModel.address2 = Functions.getText(edtAddress2);
            profileModel.city = Functions.getText(edtCity);
            profileModel.state = Functions.getText(edtState);
            profileModel.country = Functions.getText(edtCountry);
            profileModel.password = Functions.getText(edtPassword);
            profileModel.prefered_brand = Functions.getText(edtBrand);
            complexPreferences.putObject("current-retailer", profileModel);
            complexPreferences.commit();

        }

        return valid;
    }

    private void openDatePicker() {
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

    private void setCompanyDialog() {

        ArrayList<String> cats = new ArrayList<String>();
        for (int i = 0; i < companyData.company.size(); i++) {
            cats.add(companyData.company.get(i).cat_name);
        }

        String[] stringItems = new String[cats.size()];
        stringItems = cats.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, parentView);
        dialog.isTitleShow(true).show();
        dialog.title("Select Prefered Brand").titleTextSize_SP(20);

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
            map.put("retailor_id", retailerId);
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("profile_response", obj.toString());

                profileError = obj.getInt("error");
                if (profileError == 0) {
                    profileModel = new GsonBuilder().create().fromJson(obj.toString(), RetailerProfileModel.class);
                    profileModel.password = password;
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
        edtOutlet.setText(profileModel.outlet);
        edtMobile.setText(profileModel.mobile1);
        edtMobile2.setText(profileModel.mobile2);
        edtBirthDate.setText(profileModel.bdate);
        edtEmail.setText(profileModel.user_email);
        edtUsername.setText(profileModel.username);
        edtRetailer.setText(profileModel.retailerName);
        edtRegDate.setText(profileModel.registred_date);
        edtPAN.setText(profileModel.pan);
        edtTin.setText(profileModel.tin);
        edtArea.setText(profileModel.area);
        edtAddress1.setText(profileModel.address1);
        edtAddress2.setText(profileModel.address2);
        edtCity.setText(profileModel.city);
        edtState.setText(profileModel.state);
        edtCountry.setText(profileModel.country);
        edtPassword.setText(profileModel.password);
        edtRePassword.setText(profileModel.password);
        edtBrand.setText(profileModel.prefered_brand);

    }

    private class UpdateRetailer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd2 = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "update_profile");
            map.put("retailor_id", retailerId);
            map.put("shop_no", profileModel.outlet);
            map.put("mo_no", profileModel.mobile1);
            map.put("mo_no2", profileModel.mobile2);
            map.put("area", profileModel.area);
            map.put("address", profileModel.address1);
            map.put("add_1", profileModel.address2);
            map.put("city", profileModel.city);
            map.put("state", profileModel.state);
            map.put("country", profileModel.country);
            map.put("bdate", profileModel.bdate);
            map.put("display_name", profileModel.retailerName);
            map.put("pan", profileModel.pan);
            map.put("tin", profileModel.tin);
            map.put("pro_pic", "null");
            map.put("prefered_brand", selectCompanyId);
            map.put("passoword", profileModel.password);

            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("update_response", obj.toString());
                updateError = obj.getInt("error");
            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd2.dismiss();
            if (updateError == 0) {
                Functions.showSnack(parentView, "Profile updated successfully.");
            } else {
                Functions.showSnack(parentView, "Error in update profile");
            }

        }
    }
}
