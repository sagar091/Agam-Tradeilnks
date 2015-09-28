package com.example.sagar.myapplication.marketing.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.OrderMarketingData;
import com.example.sagar.myapplication.model.ProductModel;
import com.example.sagar.myapplication.model.Products;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentModeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgCart;
    View parentView;
    String orderId, orderStatus;
    String orderError;
    ProgressDialog pd;
    private TextView txtRetailer, txtOrderTotal, txtPaymentReceived, txtPaymentPending;
    Products orderData;
    MyAdapter adapter;
    ListView productsListView;
    LinearLayout otherLayout, chequeLayout, paymentScrollView;
    EditText edtAmount;
    ScrollView mainLayout;
    RadioGroup radioGroup;
    int last = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        orderId = getIntent().getStringExtra("orderId");
        orderStatus = getIntent().getStringExtra("orderStatus");

        init();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                last = checkedId;
                if (last == R.id.radio3) {
                    edtAmount.setVisibility(View.VISIBLE);
                    chequeLayout.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);
                } else if (last == R.id.radio4) {
                    chequeLayout.setVisibility(View.VISIBLE);
                    edtAmount.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);
                } else if (last == R.id.radio5) {
                    otherLayout.setVisibility(View.VISIBLE);
                    chequeLayout.setVisibility(View.GONE);
                    edtAmount.setVisibility(View.GONE);
                }
            }
        });

        new GetPaymentForOrder().execute();
    }

    private void init() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        chequeLayout = (LinearLayout) findViewById(R.id.chequeLayout);
        otherLayout = (LinearLayout) findViewById(R.id.otherLayout);
        paymentScrollView = (LinearLayout) findViewById(R.id.paymentScrollView);
        mainLayout = (ScrollView) findViewById(R.id.mainLayout);
        productsListView = (ListView) findViewById(R.id.productsListView);
        txtRetailer = (TextView) findViewById(R.id.txtRetailer);
        txtOrderTotal = (TextView) findViewById(R.id.txtOrderTotal);
        txtPaymentPending = (TextView) findViewById(R.id.txtPaymentPending);
        txtPaymentReceived = (TextView) findViewById(R.id.txtPaymentReceived);

        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Payment Mode");
        toolbar.setSubtitle("Order: " + orderId);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(PaymentModeActivity.this, CartActivity.class);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class GetPaymentForOrder extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(PaymentModeActivity.this, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "order_payment");
            map.put("order_id", orderId);
            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                Log.e("order_response", obj.toString());
                orderError = obj.getString("error");
                if (orderError.equals("0")) {
                    orderData = new GsonBuilder().create().fromJson(obj.toString(), Products.class);
                }
            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (orderError.equals("0")) {
                setDetails();
            }
        }
    }

    private void setDetails() {

        txtRetailer.setText(orderData.orders.get(0).order.retailor_name);
        txtPaymentPending.setText(getResources().getString(R.string.Rs) + " " + orderData.orders.get(0).order.payment_pending);
        txtPaymentReceived.setText(getResources().getString(R.string.Rs) + " " + orderData.orders.get(0).order.payment_recived);
        txtOrderTotal.setText(getResources().getString(R.string.Rs) + " " + orderData.orders.get(0).order.order_total);

        adapter = new MyAdapter(this, orderData.product);
        productsListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(productsListView);
        mainLayout.setVisibility(View.VISIBLE);

        if (orderStatus.equals("1")) { // completed
            paymentScrollView.setVisibility(View.GONE);
        } else {    // pending
            paymentScrollView.setVisibility(View.VISIBLE);
        }

    }

    private class MyAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        List<ProductModel> dataList;

        public MyAdapter(Context context,
                         List<ProductModel> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder mHolder;
            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.order_details_item,
                        parent, false);
                mHolder = new ViewHolder();
                mHolder.productName = (TextView) convertView
                        .findViewById(R.id.pName);
                mHolder.qty = (TextView) convertView.findViewById(R.id.pQty);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.productName.setText(dataList.get(position).product_name);
            mHolder.qty.setText(dataList.get(position).qty);
            return convertView;
        }

        private class ViewHolder {
            TextView productName, qty;
        }
    }
}
