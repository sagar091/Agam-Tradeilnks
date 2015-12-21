package com.example.sagar.myapplication.marketing.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.OrderMarketingData;
import com.example.sagar.myapplication.model.OrderModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiOrdersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    View parentView;
    OrderMarketingData orderData;
    private String userId, selectRetailerId, selectRetailerName, orderError;
    private ImageView imgCart;
    ComplexPreferences complexPreferences;
    OrdersAdapter adapter;
    private TextView noData, txtAmount;
    ListView listview;
    ProgressDialog pd;
    List<OrderModel> newOrders;
    LinearLayout paymentScrollView;
    private RadioGroup radioGroup;
    private int last = 0, payableAmount = 0;
    private Button btnSubmit;

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
        setContentView(R.layout.activity_multi_orders);

        selectRetailerId = getIntent().getStringExtra("selectRetailerId");
        selectRetailerName = getIntent().getStringExtra("selectRetailerName");

        init();

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

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        new GetRetailerOrders().execute();
    }

    private void processContinue() {

        Functions.showSnack(parentView, "In Progress..");

        /*if (last == 0) {
            Functions.showSnack(parentView, "Select payment method");

        } else if (last == R.id.radioCash) {
            enteredAmount = edtAmount.getText().toString().trim();
            if (enteredAmount.length() == 0) {
                Functions.showSnack(parentView, "Enter amount");
            } else if (Integer.parseInt(enteredAmount) > payableAmount) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                Functions.showSnack(parentView, "Proceed");
            }

        } else if (last == R.id.radioCheque) {
            enteredChequeNo = edtChequeNo.getText().toString().trim();
            enteredBankAmount = edtBankAmount.getText().toString().trim();
            enteredBankName = edtBankname.getText().toString().trim();
            enteredBankDate = edtBankDate.getText().toString().trim();

            if (enteredBankName.length() == 0 || enteredBankAmount.length() == 0 || enteredChequeNo.length() == 0 || enteredBankDate.length() == 0) {
                Functions.showSnack(parentView, "Enter Cehque details correctly");
            } else if (Integer.parseInt(enteredBankAmount) > payableAmount) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                Functions.showSnack(parentView, "Proceed");
            }

        } else if (last == R.id.radioOther) {
            enteredOtherAmount = edtOtherAmount.getText().toString().trim();
            enteredOtherDesc = edtOtherDesc.getText().toString().trim();

            if (enteredOtherAmount.length() == 0 || enteredOtherDesc.length() == 0) {
                Functions.showSnack(parentView, "Enter amount and description");
            } else if (Integer.parseInt(enteredOtherAmount) > payableAmount) {
                Functions.showSnack(parentView, "Amount should not be greater then order total.");
            } else {
                Functions.showSnack(parentView, "Proceed");
            }
        }*/
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
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        noData = (TextView) findViewById(R.id.noData);
        listview = (ListView) findViewById(R.id.listview);
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Orders");
        toolbar.setSubtitle("Retailer: " + selectRetailerName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(MultiOrdersActivity.this, CartActivity.class);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class GetRetailerOrders extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MultiOrdersActivity.this, "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "retailor_order");
            map.put("user_id", userId);
            map.put("retailor_id", selectRetailerId);
            try {
                HttpRequest request = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = request.preparePost().withData(map).sendAndReadJSON();
                orderError = obj.getString("error");

                if (orderError.equals("0")) {
                    orderData = new GsonBuilder().create().fromJson(obj.toString(), OrderMarketingData.class);
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
                newOrders = new ArrayList<>();

                for (int i = 0; i < orderData.orders.size(); i++) {
                    if (orderData.orders.get(i).order.payment_pending.equals("0")) {
                        newOrders.add(orderData.orders.get(i));
                    }
                }

                adapter = new OrdersAdapter(MultiOrdersActivity.this, newOrders);
                listview.setAdapter(adapter);
                setListViewHeightBasedOnChildren(listview);

            } else {
                noData.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // TODO Auto-generated method stub
        OrdersAdapter listAdapter = (OrdersAdapter) listView.getAdapter();
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

    private class OrdersAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        List<OrderModel> newOrders;
        int x = 0;

        public OrdersAdapter(Context context, List<OrderModel> newOrders) {
            this.context = context;
            this.newOrders = newOrders;
        }

        @Override
        public int getCount() {
            return newOrders.size();
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

            // TODO Auto-generated method stub
            final ViewHolder mHolder;
            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.ret_order_row,
                        parent, false);
                mHolder = new ViewHolder();
                mHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
                mHolder.fullLayout = (LinearLayout) convertView.findViewById(R.id.fullLayout);
                mHolder.txtOrderId = (TextView) convertView
                        .findViewById(R.id.txtOrderId);
                mHolder.txtOrderTotal = (TextView) convertView
                        .findViewById(R.id.txtOrderTotal);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.txtOrderId.setText(newOrders.get(position).order.order_id);
            mHolder.txtOrderTotal.setText(newOrders.get(position).order.order_total);

            mHolder.fullLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MultiOrdersActivity.this, PaymentModeActivity.class);
                    i.putExtra("orderId", newOrders.get(position).order.order_id);
                    i.putExtra("orderStatus", "0");
                    startActivity(i);
                }
            });

            mHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        x++;
                        payableAmount += Integer.parseInt(newOrders.get(position).order.order_total);
                    } else {
                        x--;
                        payableAmount -= Integer.parseInt(newOrders.get(position).order.order_total);
                    }

                    if (x == 0) {
                        paymentScrollView.setVisibility(View.GONE);
                        txtAmount.setVisibility(View.GONE);

                    } else {
                        txtAmount.setVisibility(View.VISIBLE);
                        Functions.showSnack(parentView, payableAmount + "");
                        paymentScrollView.setVisibility(View.VISIBLE);
                        txtAmount.setText("Payable amount:  " + getResources().getString(R.string.Rs) + " " + payableAmount);
                    }

                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView txtOrderTotal, txtOrderId;
            LinearLayout fullLayout;
            CheckBox checkbox;
        }
    }
}
