package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.sagar.myapplication.customComponent.CheckInDialog;
import com.example.sagar.myapplication.customComponent.CartDialog;
import com.example.sagar.myapplication.customComponent.SchemeViewDialog;
import com.example.sagar.myapplication.customComponent.SearchAdapter;
import com.example.sagar.myapplication.customComponent.ToolHelper;
import com.example.sagar.myapplication.customComponent.TouchImageView;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.CompanyData;
import com.example.sagar.myapplication.model.ModelClass;
import com.example.sagar.myapplication.model.ModelData;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.GsonBuilder;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeMarketingFragment extends Fragment {

    View parentView;
    private EditText edtCompany;
    CompanyData companyData;
    ProgressDialog pd;
    private String selectCompanyId;
    int modelError;
    ModelData modelData;
    private ListView productsListView;
    private ComplexPreferences complexPreferences;
    SharedPreferences preferences;
    private TextView noData;

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
        parentView = inflater.inflate(R.layout.fragment_home_marketing, container, false);
        init(parentView);

        //new GetCompany().execute();

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
                Log.e("company_response", obj.toString());
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
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");
        productsListView = (ListView) view.findViewById(R.id.productsListView);
        edtCompany = (EditText) view.findViewById(R.id.edtCompany);
        noData = (TextView) view.findViewById(R.id.noData);
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
            if (modelError == 0) {
                noData.setVisibility(View.GONE);
                //Log.e("modelData.model size", modelData.model.size() + "--");

                final SearchAdapter adapter = new MyAdapter(modelData.model, getActivity());
                productsListView.setAdapter(adapter);
            } else {
                noData.setVisibility(View.VISIBLE);
            }

        }
    }

    private class MyAdapter extends SearchAdapter<ModelClass> {

        List<ModelClass> filledContainer;
        LayoutInflater mInflater;
        Context context;
        DatabaseHandler handler;

        class ViewHolder {
            TextView txtProductName, txtProductPrice, txtProductStock;
            ImageView imgProduct;
            Button btnScheme, btnAddCart;
        }

        public MyAdapter(List<ModelClass> container, Context context) {
            super(container, context);
            filledContainer = container;
            this.context = context;
            handler = new DatabaseHandler(context);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.product_row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtProductName = (TextView) convertView.findViewById(R.id.txtProductName);
                viewHolder.txtProductPrice = (TextView) convertView.findViewById(R.id.txtProductPrice);
                viewHolder.txtProductStock = (TextView) convertView.findViewById(R.id.txtProductStock);
                viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.imgProduct);
                viewHolder.btnAddCart = (Button) convertView.findViewById(R.id.btnAddCart);
                viewHolder.btnScheme = (Button) convertView.findViewById(R.id.btnScheme);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageURL = filledContainer.get(position).image;

                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(R.layout.image_dialog);
                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    TouchImageView bigImage = (TouchImageView) dialog.findViewById(R.id.bigImage);
                    Glide.with(context).load(imageURL).thumbnail(0.1f).placeholder(R.drawable.loading).into(bigImage);

                    dialog.show();
                }
            });

            viewHolder.btnScheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SchemeViewDialog dialog = new SchemeViewDialog(getActivity(), filledContainer.get(position).schemes, "home");
                    dialog.show();
                }
            });

            viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                    if (preferences.contains("offline")) {
                        String modelName = filledContainer.get(position).name;
                        String modelId = filledContainer.get(position).id;
                        String modelPrice = filledContainer.get(position).price;

                        if (handler.productExist(modelId)) {
                            Functions.showSnack(v, "Product is already added in the cart.");
                        } else {
                            Log.e(modelId, modelName);

                            ArrayList<String> productDetails = new ArrayList<String>();
                            productDetails.add(modelId);
                            productDetails.add(modelName);
                            productDetails.add(modelPrice);

                            CartDialog dialog = new CartDialog(getActivity(), productDetails, filledContainer.get(position).schemes);
                            dialog.setOnCartAddListener(new CartDialog.OnCartAddListener() {
                                @Override
                                public void onOkClick() {
                                    Functions.showSnack(parentView, "Product added in the cart");
                                    ((MarketingDrawerActivity) getActivity()).getHelper().displayBadge();
                                }
                            });
                            dialog.show();
                        }

                    } else {
                        Log.e("offline", "blank");

                        final CheckInDialog dialog = new CheckInDialog(getActivity());
                        dialog.setOnCancelListener(new CheckInDialog.onCancelListener() {
                            @Override
                            public void setCancel() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });

            viewHolder.txtProductName.setText(filledContainer.get(position).name);
            viewHolder.txtProductPrice.setText(getResources().getString(R.string.Rs)
                    + filledContainer.get(position).price);
            Glide.with(context).load(filledContainer.get(position).image).thumbnail(0.1f).placeholder(R.drawable.loading).into(viewHolder.imgProduct);
            if (filledContainer.get(position).stock.equals("")) {
                viewHolder.txtProductStock.setVisibility(View.GONE);
            } else {
                viewHolder.txtProductStock.setVisibility(View.VISIBLE);
                viewHolder.txtProductStock.setText("Stock: " + filledContainer.get(position).stock);
            }

            return convertView;
        }
    }

}
