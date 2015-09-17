package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.ModelData;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.RetailerDrawerActivity;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;
    private String selectCompanyId, selectModelId;
    private Toolbar toolbar;
    private ImageView imgAccount;
    private EditText edtCompany, edtModel;
    private Button btnGo;
    CompanyData companyData;
    ModelData modelData;
    int modelError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new GetCompany().execute();

        edtCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (companyData.company.size() > 0) {
                    setCompanyDialog();
                } else {
                    Snackbar.make(edtCompany, "No data for company", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        edtModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectCompanyId == null) {
                    Snackbar.make(edtModel, "First Select company", Snackbar.LENGTH_SHORT).show();
                } else {
                    setModelDialog();
                }

            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, selectModelId, Snackbar.LENGTH_SHORT);
                if (selectCompanyId == null || selectModelId == null) {
                    Snackbar.make(edtModel, "Select company and model both", Snackbar.LENGTH_SHORT).show();

                } else {
                    new GetProductDetails().execute(selectModelId);
                }
            }
        });
    }

    private void setModelDialog() {
        if (modelError == 0) {
            ArrayList<String> models = new ArrayList<String>();
            for (int i = 0; i < modelData.model.size(); i++) {
                models.add(modelData.model.get(i).name);
            }

            String[] stringItems = new String[models.size()];
            stringItems = models.toArray(stringItems);

            final ActionSheetDialog dialog = new ActionSheetDialog(MainActivity.this, stringItems, edtModel);
            dialog.isTitleShow(true).show();
            dialog.title("Select Model").titleTextSize_SP(20);

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectModelId = modelData.model.get(position).id;
                    edtModel.setText(modelData.model.get(position).name);
                    dialog.dismiss();
                }
            });

        } else {
            Snackbar.make(edtModel, "No model for selected company", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setCompanyDialog() {

        ArrayList<String> cats = new ArrayList<String>();
        for (int i = 0; i < companyData.company.size(); i++) {
            cats.add(companyData.company.get(i).cat_name);
        }

        String[] stringItems = new String[cats.size()];
        stringItems = cats.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(MainActivity.this, stringItems, edtCompany);
        dialog.isTitleShow(true).show();
        dialog.title("Select Company").titleTextSize_SP(20);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCompanyId = companyData.company.get(position).cat_id;
                edtCompany.setText(companyData.company.get(position).cat_name);
                dialog.dismiss();
                edtModel.setText("");
                selectModelId = null;
                new CountDownTimer(900, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        new GetModel().execute(selectCompanyId);
                    }
                }.start();
            }
        });
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgAccount = (ImageView) findViewById(R.id.imgAccount);

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.fireIntent(MainActivity.this, LoginActivity.class);
            }
        });

        edtCompany = (EditText) findViewById(R.id.edtCompany);
        edtModel = (EditText) findViewById(R.id.edtModel);
        btnGo = (com.rey.material.widget.Button) findViewById(R.id.btnGo);
    }

    private class GetCompany extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MainActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "subcat_list");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                // Log.e("company_response", obj.toString());
                companyData = new GsonBuilder().create().fromJson(obj.toString(), CompanyData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
        }
    }

    private class GetModel extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MainActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "product_list");
            map.put("cat_id", params[0]);
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                // Log.e("model_response", obj.toString());
                modelError = obj.getInt("error");
                if (modelError == 0) {
                    modelData = new GsonBuilder().create().fromJson(obj.toString(), ModelData.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            pd.dismiss();
        }
    }

    private class GetProductDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MainActivity.this, "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "product_details");
            map.put("product_id", params[0]);
            Log.e("product_details req", map.toString());
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("product_details res", obj.toString());
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
