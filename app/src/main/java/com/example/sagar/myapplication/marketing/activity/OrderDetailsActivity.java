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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.rey.material.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView txtRetailer, txtOrderTotal, txtOrderDateTime, txtInvoiceNumber, txtInvoiceDate, txtInvoceAmount, txtRemarks, txtComments, txtPending;
    String orderStatus, orderId;
    private Toolbar toolbar;
    ProgressDialog pd;
    private String orderError;
    View parentView;
    ListView ordersListView;
    String retailor_name, order_total, date, time, invoice_number, invoice_date, invoice_amount, comments, remarks;
    CheckBox orderCompleted;
    MyAdapter adapter;
    JSONObject obj, obj1, obj2;
    ArrayList<HashMap<String, String>> orderList;
    HashMap<String, String> map1;
    HashMap<String, String> resultp = new HashMap<String, String>();
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderId = getIntent().getStringExtra("orderId");
        orderStatus = getIntent().getStringExtra("orderStatus");

        init();

        new GetOrderDetails().execute();
    }

    private void init() {
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        ordersListView = (ListView) findViewById(R.id.ordersListView);
        orderCompleted = (CheckBox) findViewById(R.id.orderCompleted);
        txtPending = (TextView) findViewById(R.id.txtPending);
        parentView = findViewById(android.R.id.content);
        txtRetailer = (TextView) findViewById(R.id.txtRetailer);
        txtOrderTotal = (TextView) findViewById(R.id.txtOrderTotal);
        txtOrderDateTime = (TextView) findViewById(R.id.txtOrderDateTime);
        txtInvoiceNumber = (TextView) findViewById(R.id.txtInvoiceNumber);
        txtInvoiceDate = (TextView) findViewById(R.id.txtInvoiceDate);
        txtInvoceAmount = (TextView) findViewById(R.id.txtInvoceAmount);
        txtRemarks = (TextView) findViewById(R.id.txtRemarks);
        txtComments = (TextView) findViewById(R.id.txtComments);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Order: " + orderId);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class GetOrderDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(OrderDetailsActivity.this, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_order_details");
            map.put("order_id", orderId);
            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                Log.e("order_response", obj.toString());
                orderError = obj.getString("error");
                if (orderError.equals("0")) {
                    retailor_name = obj.getString("retailor_name");
                    order_total = obj.getString("order_total");
                    date = obj.getString("date");
                    time = obj.getString("time");
                    invoice_number = obj.getString("invoice_number");
                    invoice_date = obj.getString("invoice_date");
                    invoice_amount = obj.getString("invoice_amount");
                    comments = obj.getString("comments");
                    remarks = obj.getString("remarks");

                    JSONArray orders = obj.getJSONArray("orders");
                    orderList = new ArrayList<>();
                    for (int i = 0; i < orders.length(); i++) {
                        map1 = new HashMap<String, String>();
                        obj1 = orders.getJSONObject(i);
                        obj2 = obj1.getJSONObject("order");
                        String productName = obj2.getString("product_name");
                        String productQty = obj2.getString("qty");
                        map1.put("product_name", productName);
                        map1.put("qty", productQty);
                        orderList.add(map1);
                    }

                }
            } catch (Exception e) {
                Functions.snack(parentView, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (orderError.equals("0")) {
                adapter = new MyAdapter(OrderDetailsActivity.this, orderList);
                ordersListView.setAdapter(adapter);
                setDetails();
            }
        }
    }

    private void setDetails() {
        txtRetailer.setText(retailor_name);
        txtInvoceAmount.setText(invoice_amount);
        txtComments.setText(comments);
        txtInvoiceDate.setText(invoice_date);
        txtInvoiceNumber.setText(invoice_number);
        txtOrderDateTime.setText(date + " - " + time);
        txtOrderTotal.setText(getResources().getString(R.string.Rs) + " " + order_total);
        txtPending.setText(getResources().getString(R.string.Rs) + " " + order_total);
        txtRemarks.setText(remarks);

        if (orderStatus.equals("1")) {
            orderCompleted.setChecked(true);
        } else {
            orderCompleted.setChecked(false);
        }
        mainLayout.setVisibility(View.VISIBLE);
    }

    private class MyAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> dataList;

        public MyAdapter(Context context,
                         ArrayList<HashMap<String, String>> dataList) {
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

            resultp = dataList.get(position);
            mHolder.productName.setText(resultp.get("product_name"));
            mHolder.qty.setText(resultp.get("qty"));
            return convertView;
        }

        private class ViewHolder {
            TextView productName, qty;
        }
    }
}
