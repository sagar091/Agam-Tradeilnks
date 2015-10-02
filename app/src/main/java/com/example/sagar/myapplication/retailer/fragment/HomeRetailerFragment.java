package com.example.sagar.myapplication.retailer.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.ModelData;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeRetailerFragment extends Fragment {

    View parentView;
    ProgressDialog pd;
    private String selectCompanyId, selectModelId;
    private EditText edtCompany, edtModel;
    private Button btnGo;
    CompanyData companyData;
    ModelData modelData;
    int modelError;
    private ComplexPreferences complexPreferences;

    public static HomeRetailerFragment newInstance(String param1, String param2) {
        HomeRetailerFragment fragment = new HomeRetailerFragment();

        return fragment;
    }

    public HomeRetailerFragment() {
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
        parentView = inflater.inflate(R.layout.activity_main, container, false);

        init(parentView);

        companyData = new CompanyData();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        companyData = complexPreferences.getObject("mobile_companies", CompanyData.class);

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

        return parentView;
    }

    private void setCompanyDialog() {

        ArrayList<String> cats = new ArrayList<String>();
        for (int i = 0; i < companyData.company.size(); i++) {
            cats.add(companyData.company.get(i).cat_name);
        }

        String[] stringItems = new String[cats.size()];
        stringItems = cats.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, edtCompany);
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

    private void init(View parentView) {
        ((RetailerDrawerActivity) getActivity()).setTitle("Products");
        ((RetailerDrawerActivity) getActivity()).setSubtitle("no");

        edtCompany = (EditText) parentView.findViewById(R.id.edtCompany);
        edtModel = (EditText) parentView.findViewById(R.id.edtModel);
        btnGo = (com.rey.material.widget.Button) parentView.findViewById(R.id.btnGo);
    }

    private void setModelDialog() {
        if (modelError == 0) {
            ArrayList<String> models = new ArrayList<String>();
            for (int i = 0; i < modelData.model.size(); i++) {
                models.add(modelData.model.get(i).name);
            }

            String[] stringItems = new String[models.size()];
            stringItems = models.toArray(stringItems);

            final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, edtModel);
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

    private class GetCompany extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "subcat_list");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
//                Log.e("company_response", obj.toString());
                companyData = new GsonBuilder().create().fromJson(obj.toString(), CompanyData.class);
                if (companyData.company.size() > 0) {
                    complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                    complexPreferences.putObject("mobile_companies", companyData);
                    complexPreferences.commit();
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
        }
    }

    private class GetModel extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
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
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
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
