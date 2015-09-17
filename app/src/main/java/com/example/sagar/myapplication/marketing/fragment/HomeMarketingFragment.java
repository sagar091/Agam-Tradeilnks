package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.SearchAdapter;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.ModelClass;
import com.example.sagar.myapplication.model.ModelData;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeMarketingFragment extends Fragment {

    private EditText edtCompany;
    CompanyData companyData;
    ProgressDialog pd;
    private String selectCompanyId;
    int modelError;
    ModelData modelData;
    private ListView productsListView;

    public static HomeMarketingFragment newInstance(String param1, String param2) {
        HomeMarketingFragment fragment = new HomeMarketingFragment();
        return fragment;
    }

    public HomeMarketingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home_marketing, container, false);
        init(view);

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

        return view;

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
                new CountDownTimer(900, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        new GetProducts().execute(selectCompanyId);
                    }
                }.start();
            }
        });
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

    private void init(View view) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Products");
        productsListView = (ListView) view.findViewById(R.id.productsListView);
        edtCompany = (EditText) view.findViewById(R.id.edtCompany);
    }

    private class GetProducts extends AsyncTask<String, Void, String> {

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
            Log.e("product_req", map.toString());
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("products_response", obj.toString());
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            Log.e("modelData.model size", modelData.model.size() + "--");

            final SearchAdapter adapter = new MyAdapter(modelData.model, getActivity());
            productsListView.setAdapter(adapter);
        }
    }

    private class MyAdapter extends SearchAdapter<ModelClass> {

        List<ModelClass> filledContainer;
        LayoutInflater mInflater;

        class ViewHolder {
            TextView txtProductName, txtProductPrice, txtProductStock;
            ImageView imgProduct;
            Button btnScheme, btnAddCart;
        }

        public MyAdapter(List<ModelClass> container, Context context) {
            super(container, context);
            filledContainer = container;
            Log.e("filledContainer", filledContainer.size() + "--");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.product_row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtProductName = (TextView) convertView.findViewById(R.id.txtProductName);
                viewHolder.txtProductPrice = (TextView) convertView.findViewById(R.id.txtProductPrice);
                viewHolder.txtProductStock = (TextView) convertView.findViewById(R.id.txtProductStock);
                viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.imgProduct);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtProductName.setText(filledContainer.get(position).name);
            viewHolder.txtProductPrice.setText(filledContainer.get(position).price);
            Glide.with(context).load(filledContainer.get(position).image).into(viewHolder.imgProduct);
            viewHolder.txtProductStock.setText(filledContainer.get(position).stock);

            return convertView;
        }
    }

}
