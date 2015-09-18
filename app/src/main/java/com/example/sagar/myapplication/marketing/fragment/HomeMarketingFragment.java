package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.AddCartDialog;
import com.example.sagar.myapplication.customComponent.SearchAdapter;
import com.example.sagar.myapplication.customComponent.TouchImageView;
import com.example.sagar.myapplication.helper.Constants;
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

import me.gujun.android.taggroup.TagGroup;

public class HomeMarketingFragment extends Fragment {

    private EditText edtCompany;
    CompanyData companyData;
    ProgressDialog pd;
    private String selectCompanyId;
    int modelError;
    ModelData modelData;
    private ListView productsListView;
    Dialog cartDialog;

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

            viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedModel = filledContainer.get(position).name;
                    String selectedModelId = filledContainer.get(position).id;
                    final String selectModelUnitPrice = filledContainer.get(position).price;
                    Log.e(selectedModelId, selectedModel);

                    AddCartDialog custom5 = new AddCartDialog(getActivity(), R.style.CustomDialogsTheme);
                    custom5.show();

                    /*View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_cart_dialog, null);
                    cartDialog = new Dialog(getActivity(), R.style.CustomDialogsTheme);
                    cartDialog.setContentView(dialogView);

                    *//*cartDialog.setContentView(R.layout.add_cart_dialog);
                    WindowManager.LayoutParams params = cartDialog.getWindow().getAttributes();
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    cartDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);*//*

                    TextView unitPrice = (TextView) cartDialog.findViewById(R.id.unitPrice);
                    final TextView unitQty = (TextView) cartDialog.findViewById(R.id.unitQty);
                    final TextView unitTotalPrice = (TextView) cartDialog.findViewById(R.id.unitTotalPrice);
                    //  TagGroup mTagGroup = (TagGroup)dialog.findViewById(R.id.tag_group);

                    unitPrice.setText(getResources().getString(R.string.Rs) + " " + selectModelUnitPrice);
                    unitQty.setText("x 1 Qty");
                    unitTotalPrice.setText("= " + getResources().getString(R.string.Rs) + " " + selectModelUnitPrice);

                    SeekBar seekBar = (SeekBar) cartDialog.findViewById(R.id.seekBar);
                    seekBar.setMax(100);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (progress != 0) {
                                unitQty.setText("x " + progress + " Qty");
                                unitTotalPrice.setText("= " + getResources().getString(R.string.Rs) + " " + String.valueOf(Integer.parseInt(selectModelUnitPrice) * progress));
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    cartDialog.show();*/
                }
            });

            viewHolder.txtProductName.setText(filledContainer.get(position).name);
            viewHolder.txtProductPrice.setText(getResources().getString(R.string.Rs)
                    + filledContainer.get(position).price);
            Glide.with(context).load(filledContainer.get(position).image).thumbnail(0.1f).placeholder(R.drawable.loading).into(viewHolder.imgProduct);
            viewHolder.txtProductStock.setText("Stock: " + filledContainer.get(position).stock);

            return convertView;
        }
    }

}
