package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
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
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String userName, password;
    private EditText edtUserName, edtPassword;
    private CheckBox showPassword;
    private ImageView imgCart;
    private Button loginButton;
    private ProgressDialog pDialog;

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

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        showPassword = (CheckBox) findViewById(R.id.showPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processLogin();
            }
        });
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
            callAPI();
        }
    }

    private void callAPI() {
    }
}
