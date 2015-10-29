package com.example.sagar.myapplication.marketing.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.AskDialog;
import com.example.sagar.myapplication.customComponent.SchemeViewDialog;
import com.example.sagar.myapplication.customComponent.UpdateCartDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.ModelData;
import com.example.sagar.myapplication.model.ProductCart;
import com.example.sagar.myapplication.model.Scheme;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    DatabaseHandler handler;
    private EditText edtRemarks;
    private TextView txtTotal;
    View parentView;
    List<ProductCart> products = new ArrayList<>();
    private Toolbar toolbar;
    private ImageView imgCart;
    private TextView emptyCart;
    private ListView productsListView;
    private CartAdapter adapter;
    private RelativeLayout cartLayout;
    int total = 0;
    private Button btnPlaceOrder;
    ProgressDialog pd;
    ComplexPreferences complexPreferences;
    private String userId, retailerId, retailerType;
    SharedPreferences preferences;
    int schemeError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        displayProducts();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                retailerId = preferences.getString("offline", null);
                retailerType = preferences.getString("retailer_type", null);

                Log.e("retailerType", retailerType);

                products = new ArrayList<>();
                products.clear();
                handler = new DatabaseHandler(CartActivity.this);
                try {
                    handler.openDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                products = handler.getProducts();
                new PlaceOrder().execute();
            }
        });

    }

    private void displayProducts() {

        products = new ArrayList<>();
        products.clear();
        handler = new DatabaseHandler(CartActivity.this);
        try {
            handler.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        products = handler.getProducts();

        Log.e("products", products.size() + "--");

        if (products.size() == 0) {
            emptyCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);

        } else {
            total = 0;
            emptyCart.setVisibility(View.GONE);

            for (int i = 0; i < products.size(); i++) {
                total += Integer.parseInt(products.get(i).getSubTotal());
            }
            Log.e("total", total + "--");

            txtTotal.setText("Total: " + getResources().getString(R.string.Rs) + " " + total);

            //productsListView.removeFooterView(footerView);
            adapter = new CartAdapter(this, products);
            adapter.notifyDataSetChanged();
            productsListView.setAdapter(adapter);
            cartLayout.setVisibility(View.VISIBLE);
        }
    }

    private void init() {

        edtRemarks = (EditText) findViewById(R.id.edtRemarks);
        txtTotal = (TextView) findViewById(R.id.txtTotal);

        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        parentView = findViewById(android.R.id.content);
        cartLayout = (RelativeLayout) findViewById(R.id.cartLayout);
        productsListView = (ListView) findViewById(R.id.productsListView);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        if (toolbar != null) {
            toolbar.setTitle("Your Cart");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private class CartAdapter extends BaseAdapter {

        Context context;
        List<ProductCart> products;
        LayoutInflater mInflater;

        public CartAdapter(Context context, List<ProductCart> products) {
            this.context = context;
            this.products = products;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cart_item, parent,
                        false);
                mHolder = new ViewHolder();
                mHolder.txtProductName = (TextView) convertView
                        .findViewById(R.id.txtProductName);
                mHolder.txtProductPrice = (TextView) convertView
                        .findViewById(R.id.txtProductPrice);
                mHolder.txtColors = (TextView) convertView
                        .findViewById(R.id.txtColors);
                mHolder.txtQty = (TextView) convertView.findViewById(R.id.txtQty);
                mHolder.txtSelectedScheme = (TextView) convertView.findViewById(R.id.txtSelectedScheme);
                mHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
                mHolder.btnScheme = (Button) convertView.findViewById(R.id.btnScheme);
                mHolder.edit = (ImageView) convertView.findViewById(R.id.qty);
                mHolder.txtSubtotal = (TextView) convertView.findViewById(R.id.txtSubtotal);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            String str = "<b>Name: </b>" + products.get(position).getName();
            mHolder.txtProductName.setText(Html.fromHtml(str));

            str = "<b>Price: </b>" + getResources().getString(R.string.Rs)
                    + products.get(position).getSubTotal();
            mHolder.txtProductPrice.setText(Html.fromHtml(str));

            mHolder.txtQty.setText(products.get(position).getQty());

            str = "<b>Colors: </b>" + products.get(position).getColors();
            mHolder.txtColors.setText(Html.fromHtml(str));

            if (products.get(position).getSchemeId() == 0) {
                str = "<b>Sheme: </b>" + "No scheme";
                mHolder.txtSubtotal.setVisibility(View.GONE);

            } else {
                mHolder.txtSubtotal.setVisibility(View.VISIBLE);
                String str1 = "<b>Price: </b>" + getResources().getString(R.string.Rs)
                        + products.get(position).getSubTotal();
                mHolder.txtSubtotal.setText(Html.fromHtml(str1));
                str = "<b>Scheme: </b>" + products.get(position).getSchemeText();
            }

            mHolder.txtSelectedScheme.setText(Html.fromHtml(str));

            mHolder.btnScheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler = new DatabaseHandler(context);
                    List<Scheme> schemes = handler.getScheme(products.get(position).getProductId());

                    SchemeViewDialog dialog = new SchemeViewDialog(context, schemes, "cart");
                    dialog.setOnApplyListener(new SchemeViewDialog.OnApplyListener() {
                        @Override
                        public void onApplyClick(int schemeId, String scheme) {
                            handler = new DatabaseHandler(context);
                            if (schemeId == 0) {
                                scheme = "No scheme";
                                mHolder.txtSubtotal.setVisibility(View.GONE);

                            } else {
                                mHolder.txtSubtotal.setVisibility(View.VISIBLE);
                                String str = "<b>Price: </b>" + getResources().getString(R.string.Rs)
                                        + products.get(position).getSubTotal();
                                mHolder.txtSubtotal.setText(Html.fromHtml(str));
                            }

                            new ApplyScheme().execute(position, schemeId);

                            handler.addScheme(products.get(position).getProductId(), schemeId, scheme);
                            displayProducts();
                            adapter.notifyDataSetChanged();
                        }

                    });
                    dialog.show();
                }
            });

            mHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = products.get(position).getProductId();
                    String colors = products.get(position).getColors();
                    String qty = products.get(position).getQty();
                    String price = products.get(position).getPrice();

                    String[] diff = colors.split(", ");
                    List<String> diffColors = new ArrayList<String>();
                    for (int k = 0; k < diff.length; k++) {
                        diffColors.add(diff[k]);
                    }

                    UpdateCartDialog updateCartDialog = new UpdateCartDialog(context, productId, diffColors, qty, price);
                    updateCartDialog.setOnCartAddListener(new UpdateCartDialog.OnCartAddListener() {
                        @Override
                        public void onOkClick() {
                            Functions.showSnack(parentView, "Product updated");
                            // productsListView.removeFooterView(footerView);
                            displayProducts();

                        }
                    });
                    updateCartDialog.show();
                }
            });

            mHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AskDialog askDialog = new AskDialog(context, "Do you want to remove this product from cart?");
                    askDialog.setOnYesListener(new AskDialog.OnYesClickListener() {
                        @Override
                        public void clickYes() {
                            handler = new DatabaseHandler(context);
                            handler.deleteCartProduct(products.get(position).getProductId());
//                            productsListView.removeFooterView(footerView);
                            displayProducts();
                        }
                    });
                    askDialog.show();
                }
            });

            //productsListView.addFooterView(footerView);
            return convertView;
        }

        private class ViewHolder {
            TextView txtProductName, txtProductPrice, txtColors, txtSelectedScheme, txtSubtotal;
            TextView txtQty;
            ImageView remove;
            ImageView edit;
            Button btnScheme;
        }

        private class ApplyScheme extends AsyncTask<Integer, Void, Integer> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(context, "Loading", "Please wait", false);
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                HashMap<String, String> map = new HashMap<>();
                map.put("form_type", "select_scheme");
                map.put("product_id", products.get(params[0]).getProductId());
                map.put("scheme_id", params[1] + "");
                map.put("product_qty", products.get(params[0]).getQty());
                map.put("price", products.get(params[0]).getPrice());

                Log.e("scheme_req", map.toString());
                try {
                    HttpRequest req = new HttpRequest(Constants.BASE_URL);
                    JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                    Log.e("scheme_response", obj.toString());
                    schemeError = obj.getInt("error");
                    if (schemeError == 0) {
//                        modelData = new GsonBuilder().create().fromJson(obj.toString(), ModelData.class);
                    }else{
                        Functions.showSnack(parentView,obj.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                pd.dismiss();
            }
        }
    }

    private class PlaceOrder extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(CartActivity.this, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "create_order");

            for (int i = 0; i < products.size(); i++) {
                map.put("product[" + i + "]", products.get(i).getProductId());
                map.put("qty[" + i + "]", products.get(i).getQty());
                map.put("scheme[" + i + "]", products.get(i).getSchemeId() + "");
            }

            map.put("user_id", userId);
            map.put("order_type", retailerType);
            map.put("retailor_id", retailerId);
            map.put("remarks", Functions.getText(edtRemarks));
            map.put("order_total", total + "");

            Log.e("place_order", map.toString());

            /*try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("place_order_response", obj.toString());
                *//*statusObject = obj.getJSONObject("status");

                loginError = statusObject.getInt("error");
                Log.e("loginError", loginError + "");
                if (loginError == 0) {
                    Log.e("login", "success");
                    userProfile = new GsonBuilder().create().fromJson(statusObject.toString(), UserProfile.class);

                    userProfile.password = Functions.getText(edtPassword);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                    complexPreferences.putObject("current-user", userProfile);
                    complexPreferences.commit();

                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isUserLogin", true);
                    editor.commit();

                } else {
                    Log.e("login", "un-success");
                    Snackbar.make(loginButton, "Invalid Login Credentials", Snackbar.LENGTH_LONG).show();
                }*//*

            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
        }
    }
}
