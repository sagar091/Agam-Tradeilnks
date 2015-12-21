package com.example.sagar.myapplication.marketing.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.sagar.myapplication.customComponent.AskDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.ProductModel;
import com.example.sagar.myapplication.model.Products;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PaymentModeActivity extends AppCompatActivity {

    private Button btnSubmit;
    private Toolbar toolbar;
    private ImageView imgCart;
    private View parentView;
    private String orderId, orderStatus, orderError, paymentType, paymentCompleteStatus;
    private ProgressDialog pd;
    private TextView txtRetailer, txtOrderTotal, txtPaymentReceived, txtPaymentPending;
    private Products orderData;
    private MyAdapter adapter;
    private ListView productsListView;
    private LinearLayout paymentScrollView;
    private ScrollView mainLayout;
    private RadioGroup radioGroup;
    private int last = 0;
    ComplexPreferences complexPreferences;
    private String userId, retailerId;
    SharedPreferences preferences;

    // Cash
    EditText edtAmount;
    private String enteredAmount;

    // Cheque
    EditText edtChequeNo, edtBankname, edtBankAmount, edtBankDate;
    LinearLayout chequeLayout;
    private String enteredChequeNo, enteredBankName, enteredBankAmount, enteredBankDate;

    // Other
    EditText edtOtherAmount, edtOtherDesc;
    LinearLayout otherLayout;
    private String enteredOtherAmount, enteredOtherDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        orderId = getIntent().getStringExtra("orderId");
        orderStatus = getIntent().getStringExtra("orderStatus");

        init();

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        retailerId = preferences.getString("offline", null);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                last = checkedId;
                if (last == R.id.radioCash) {
                    edtAmount.setVisibility(View.VISIBLE);
                    chequeLayout.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);
                } else if (last == R.id.radioCheque) {
                    chequeLayout.setVisibility(View.VISIBLE);
                    edtAmount.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);
                } else if (last == R.id.radioOther) {
                    otherLayout.setVisibility(View.VISIBLE);
                    chequeLayout.setVisibility(View.GONE);
                    edtAmount.setVisibility(View.GONE);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processContinue();
            }
        });

        new GetPaymentForOrder().execute();
    }

    private void processContinue() {

        if (last == 0) {
            Functions.showSnack(parentView, "Select payment method");

        } else if (last == R.id.radioCash) { // Cash
            enteredAmount = edtAmount.getText().toString().trim();
            if (enteredAmount.length() == 0) {
                Functions.showSnack(parentView, "Enter amount");
            } else if (Integer.parseInt(enteredAmount) > Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                paymentType = "0";
                if (Integer.parseInt(enteredAmount) == Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                    paymentCompleteStatus = "1";
                } else {
                    paymentCompleteStatus = "0";
                }
                confirmPayment();

            }

        } else if (last == R.id.radioCheque) { // Cheque
            enteredChequeNo = edtChequeNo.getText().toString().trim();
            enteredBankAmount = edtBankAmount.getText().toString().trim();
            enteredBankName = edtBankname.getText().toString().trim();
            enteredBankDate = edtBankDate.getText().toString().trim();

            if (enteredBankName.length() == 0 || enteredBankAmount.length() == 0 || enteredChequeNo.length() == 0 || enteredBankDate.length() == 0) {
                Functions.showSnack(parentView, "Enter Cehque details correctly");
            } else if (Integer.parseInt(enteredBankAmount) > Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                paymentType = "1";
                if (Integer.parseInt(enteredBankAmount) == Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                    paymentCompleteStatus = "1";
                } else {
                    paymentCompleteStatus = "0";
                }
                confirmPayment();

            }

        } else if (last == R.id.radioOther) { // Other
            enteredOtherAmount = edtOtherAmount.getText().toString().trim();
            enteredOtherDesc = edtOtherDesc.getText().toString().trim();

            if (enteredOtherAmount.length() == 0 || enteredOtherDesc.length() == 0) {
                Functions.showSnack(parentView, "Enter amount and description");
            } else if (Integer.parseInt(enteredOtherAmount) > Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                paymentType = "2";
                if (Integer.parseInt(enteredOtherAmount) == Integer.parseInt(orderData.orders.get(0).order.order_total)) {
                    paymentCompleteStatus = "1";
                } else {
                    paymentCompleteStatus = "0";
                }
                confirmPayment();

            }
        }
    }

    private void confirmPayment() {
        AskDialog askDialog = new AskDialog(this, "Are you sure want to do payment for this order?");
        askDialog.setOnYesListener(new AskDialog.OnYesClickListener() {
            @Override
            public void clickYes() {
                new PaymentProcess().execute();
            }
        });
        askDialog.show();
    }

    private void init() {
        edtChequeNo = (EditText) findViewById(R.id.edtChequeNo);
        edtBankname = (EditText) findViewById(R.id.edtBankname);
        edtBankAmount = (EditText) findViewById(R.id.edtBankAmount);
        edtBankDate = (EditText) findViewById(R.id.edtBankDate);
        edtOtherAmount = (EditText) findViewById(R.id.edtOtherAmount);
        edtOtherDesc = (EditText) findViewById(R.id.edtOtherDesc);
        edtAmount = (EditText) findViewById(R.id.edtAmount);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

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

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // TODO Auto-generated method stub
        MyAdapter listAdapter = (MyAdapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 30;
        listView.setLayoutParams(params);
        listView.requestLayout();
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

    private class PaymentProcess extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "update_order_payment");
            map.put("order_id", orderId);
            map.put("payment_amount", enteredAmount);
            map.put("payment_type", paymentType);
            map.put("is_complted", paymentCompleteStatus);
            map.put("check_amount", enteredBankAmount);
            map.put("check_number", enteredChequeNo);
            map.put("check_date", enteredBankDate);
            map.put("bank_name", enteredBankName);
            map.put("market_id", userId);
            map.put("retailor_id", retailerId);
            map.put("other_amount", enteredOtherAmount);
            map.put("other_desc", enteredOtherDesc);

            Log.e("payement_req", map.toString());

            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                Log.e("payment_response", obj.toString());
                orderError = obj.getString("error");
                if (orderError.equals("0")) {
                    orderData = new GsonBuilder().create().fromJson(obj.toString(), Products.class);
                    Functions.showSnack(parentView, "Order update");
                    finish();
                }
            } catch (Exception e) {
                Functions.showSnack(parentView, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
