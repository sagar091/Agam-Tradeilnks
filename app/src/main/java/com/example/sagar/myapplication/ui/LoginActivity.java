package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.RetailerDrawerActivity;
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
        }
    }

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
                JSONObject statusObject = obj.getJSONObject("status");

                loginError = statusObject.getInt("error");
                if (loginError == 0) {
                    userProfile = new GsonBuilder().create().fromJson(statusObject.toString(), UserProfile.class);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                    complexPreferences.putObject("current-user", userProfile);
                    complexPreferences.commit();

                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isUserLogin", true);
                    editor.commit();

                    if (userProfile.user_type.equals("1")) {
                        Snackbar.make(loginButton, "Login as Marketing Executive", Snackbar.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, CheckInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Snackbar.make(loginButton, "Login as Retailer", Snackbar.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, RetailerDrawerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

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
            loginButton.setText("Login");
        }
    }
}
