package com.example.sagar.myapplication.helper;

/**
 * @author jatin
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions {

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void showToast(Context ctx, String msg) {

        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void fireIntent(Activity activity, Class cls) {

        Intent i = new Intent(activity, cls);
        activity.startActivity(i);

    }

    public static boolean emailValidation(String email) {
        boolean validEmailAddress = true;
        if (email.length() == 0) {
            validEmailAddress = false;
        } else {
            if (!email.contains(".") || !email.contains("@")) {
                validEmailAddress = false;
            } else {
                int index1 = email.indexOf("@");
                String subStringType = email.substring(index1);
                int index2 = index1 + subStringType.indexOf(".");
                if (index1 == 0 || index2 == 0) {
                    validEmailAddress = false;
                } else {
                    String typeOf = email.substring(index1, index2);
                    if (typeOf.length() < 1) {
                        validEmailAddress = false;
                    }
                    String typeOf2 = email.substring(index2);
                    if (typeOf2.length() < 2) {
                        validEmailAddress = false;
                    }
                }

            }
        }

        return validEmailAddress;
    }

    public static String parseDate(String inputDate, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void showSnack(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void hideKeyPad(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isConnecting(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getLength(EditText edtText){
        return edtText.getText().toString().length();
    }

}