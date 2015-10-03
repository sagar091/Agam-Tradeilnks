package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.FirstTimeDialog;
import com.example.sagar.myapplication.customComponent.HomeWatcher;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.RetailerProfileModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String userName, password;
    private EditText edtUserName, edtPassword;
    private CheckBox showPassword;
    private ImageView imgCart;
    private Button loginButton;
    private ProgressDialog pd;
    UserProfile userProfile;
    private int loginError;
    JSONObject statusObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.hideKeyPad(LoginActivity.this, view);
                processLogin();
            }
        });

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        showPassword = (CheckBox) findViewById(R.id.showPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

    }

    private void processLogin() {
        userName = edtUserName.getText().toString().trim();
        password = edtPassword.getText().toString().trim();

        if (userName.length() == 0) {
            Snackbar.make(edtUserName, "Username is required.", Snackbar.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Snackbar.make(edtUserName, "Password is required.", Snackbar.LENGTH_SHORT).show();
        } else {
            loginButton.setText("Processing..");
            new doLogin().execute();

//            loginProcess();
        }
    }

    /*private void loginProcess() {
        pd = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait", false);
        HashMap<String, String> map = new HashMap<>();
        map.put("form_type", "user_login");
        map.put("username", userName);
        map.put("password", password);

        Fuel.post(Constants.BASE_URL, map).responseJson(new Handler<JSONObject>() {
            @Override
            public void success(Request request, Response response, JSONObject jsonObject) {
                pd.dismiss();
                Log.e("login_response", jsonObject.toString());
                try {
                    JSONObject statusObject = jsonObject.getJSONObject("status");
                    String error = statusObject.getString("error");
                    Log.e("error", error);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(Request request, Response response, FuelError fuelError) {
                pd.dismiss();
            }
        });
    }
*/
    private class doLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "user_login");
            map.put("username", userName);
            map.put("password", password);
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("login_response", obj.toString());
                statusObject = obj.getJSONObject("status");

                loginError = statusObject.getInt("error");
                if (loginError == 0) {
                    userProfile = new GsonBuilder().create().fromJson(statusObject.toString(), UserProfile.class);

                    userProfile.password = Functions.getText(edtPassword);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                    complexPreferences.putObject("current-user", userProfile);
                    complexPreferences.commit();

                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isUserLogin", true);
                    editor.commit();

                } else {
                    Snackbar.make(loginButton, "Invalid Login Credentials", Snackbar.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();

            if (userProfile.user_type.equals("1")) {
                Intent intent = new Intent(LoginActivity.this, CheckInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {

                if (userProfile.is_new == null) {
                    Intent intent = new Intent(LoginActivity.this, RetailerDrawerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else if (userProfile.is_new.equals("1")) {

                    HomeWatcher mHomeWatcher = new HomeWatcher(LoginActivity.this);
                    mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
                        @Override
                        public void onHomePressed() {
                            // do something here...
                            Functions.closeSession(LoginActivity.this);

                        }

                        @Override
                        public void onHomeLongPressed() {
                        }
                    });

                    mHomeWatcher.startWatch();

                    FirstTimeDialog dialog = new FirstTimeDialog(LoginActivity.this);
                    dialog.setOnChangePasswordListener(new FirstTimeDialog.onChangePasswordListener() {
                        @Override
                        public void setPassword(String password) {
                            userProfile = new GsonBuilder().create().fromJson(statusObject.toString(), UserProfile.class);

                            userProfile.password = password;

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                            complexPreferences.putObject("current-user", userProfile);
                            complexPreferences.commit();
                        }
                    });
                    dialog.show();
                }
            }

            loginButton.setText("Login");
        }

    }
}
