package com.example.sagar.myapplication.customComponent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.UserProfile;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class ChangePasswordDialog extends BaseDialog {

    View parentView;
    Button btnSubmit;
    private EditText edtConfirmPassword, edtPassword, edtBdate;
    ProgressDialog pd;
    private String userId;
    ComplexPreferences complexPreferences;
    String password, passwordError, msg;
    private int type = 0;
    private String username = "";
    SuccessListener onSuccessListener;

    public void setOnSuccessListener(SuccessListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
    }

    public ChangePasswordDialog(Context context, int type, String username) {
        super(context);
        this.type = type;
        this.username = username;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.change_password, null);
        init();

        if (type == 1) {
            UserProfile userProfile = new UserProfile();
            complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_pref", 0);
            userProfile = complexPreferences.getObject("current-user", UserProfile.class);
            userId = userProfile.user_id;
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return parentView;
    }

    private void init() {
        edtBdate = (EditText) parentView.findViewById(R.id.edtBdate);
        if (type == 0) {
            edtBdate.setVisibility(View.VISIBLE);
        } else {
            edtBdate.setVisibility(View.GONE);
        }
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);
        edtPassword = (EditText) parentView.findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) parentView.findViewById(R.id.edtConfirmPassword);

        edtBdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                edtBdate.setText(new StringBuilder().append(dayOfMonth).append("-")
                                        .append(monthOfYear + 1).append("-").append(year));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(Calendar.getInstance());
                dpd.show(((AppCompatActivity) context).getFragmentManager(), "Select Birthdate");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideKeyPad(context, v);

                if (type == 0) {
                    if (Functions.getLength(edtBdate) == 0) {
                        Functions.showSnack(parentView, "Date of Birth is required");
                    } else {
                        checkDialog();
                    }
                } else {
                    checkDialog();
                }
            }
        });

    }

    private void checkDialog() {
        if (edtPassword.getText().toString().trim().length() == 0) {
            Functions.showSnack(parentView, "Password is required");
        } else if (!edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
            Functions.showSnack(parentView, "Password and Confirm password must be same");
        } else {
            password = edtPassword.getText().toString().trim();
            new ChangePassword().execute();
        }
    }

    @Override
    public boolean setUiBeforShow() {

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
            if (type == 0) {
                map.put("form_type", "update_password");
                map.put("user_id", username);
                map.put("birthdate", Functions.getText(edtBdate));
            } else {
                map.put("form_type", "change_password_market");
                map.put("user_id", userId);
            }
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
                if (onSuccessListener != null) {
                    onSuccessListener.onSucces();
                }
            } else {
                Functions.showSnack(parentView, msg);
            }
        }
    }

    public interface SuccessListener {
        public void onSucces();
    }
}
