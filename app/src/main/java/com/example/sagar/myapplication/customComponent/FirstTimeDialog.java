package com.example.sagar.myapplication.customComponent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.RetailerProfileModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;
import com.example.sagar.myapplication.ui.MainActivity;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class FirstTimeDialog extends BaseDialog {

    View parentView;
    Button btnSubmit, btnCancel;
    private EditText edtConfirmPassword, edtPassword;
    ProgressDialog pd;
    private String userId;
    ComplexPreferences complexPreferences;
    String password, passwordError, msg;

    public void setOnChangePasswordListener(FirstTimeDialog.onChangePasswordListener onChangePasswordListener) {
        this.onChangePasswordListener = onChangePasswordListener;
    }

    onChangePasswordListener onChangePasswordListener;

    public FirstTimeDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.first_time, null);
        init(parentView);

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        return parentView;
    }

    private void init(final View parentView) {
        btnCancel = (Button) parentView.findViewById(R.id.btnCancel);
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);
        edtPassword = (EditText) parentView.findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) parentView.findViewById(R.id.edtConfirmPassword);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideKeyPad(context, v);
                if (edtPassword.getText().toString().trim().length() < 6) {
                    Functions.showSnack(parentView, "6 digit Password required");
                } else if (!edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
                    Functions.showSnack(parentView, "Password and Confirm password must be same");
                } else {
                    password = edtPassword.getText().toString().trim();
                    new ChangePassword().execute();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.closeSession(context);

                dismiss();

                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    @Override
    public boolean setUiBeforShow() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        return false;
    }

    private class ChangePassword extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "change_password_retail");
            map.put("user_id", userId);
            map.put("password", password);
            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                Log.e("forget_response", obj.toString());
                passwordError = obj.getString("error");
                msg = obj.getString("msg");
            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (passwordError.equals("0")) {
                dismiss();

                if (onChangePasswordListener != null) {
                    onChangePasswordListener.setPassword(password);
                }

                Intent intent = new Intent(context, RetailerDrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Functions.showSnack(parentView, msg);
            }
        }
    }

    public interface onChangePasswordListener {
        public void setPassword(String password);
    }
}
