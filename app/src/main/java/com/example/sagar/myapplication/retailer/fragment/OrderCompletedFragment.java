package com.example.sagar.myapplication.retailer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.OrderDetailsActivity;
import com.example.sagar.myapplication.model.OrderMarketingData;
import com.example.sagar.myapplication.model.OrderModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class OrderCompletedFragment extends Fragment {

    private ListView listView;
    View parentView;
    ComplexPreferences complexPreferences;
    private String userId;
    private int orderError;
    ProgressDialog pd;
    OrderMarketingData orderData;
    CompletedOrderAdapter adapter;
    String orderId, orderStatus;
    private TextView noData;

    public static OrderCompletedFragment newInstance() {
        OrderCompletedFragment fragment = new OrderCompletedFragment();
        return fragment;
    }

    public OrderCompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_order_pending, container, false);
        init(parentView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderId = orderData.orders.get(position).order.order_id;
                orderStatus = "1";

                Intent i = new Intent(getActivity(), OrderDetailsActivity.class);
                i.putExtra("orderId", orderId);
                i.putExtra("orderStatus", orderStatus);
                startActivity(i);
            }
        });

        return parentView;
    }

    private void init(View parentView) {
        findViewById(parentView);

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        new GetPendingOrder().execute();

    }

    private void findViewById(View parentView) {
        noData = (TextView) parentView.findViewById(R.id.noData);
        listView = (ListView) parentView.findViewById(R.id.listView);
    }

    private class GetPendingOrder extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Fetching Orders.", false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_completed_orders_retailors");
            map.put("retailor_id", userId);
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                //Log.e("order_pending_response", obj.toString());
                orderError = obj.getInt("error");
                if (orderError == 0) {
                    orderData = new GsonBuilder().create().fromJson(obj.toString(), OrderMarketingData.class);
                } else {
                    orderData = null;
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
                orderData = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            if (orderData == null) {
                Log.e("no data", "no data");
            }
 /*           if (orderData.orders != null || orderData.orders.size() > 0) {
                noData.setVisibility(View.GONE);
                adapter = new CompletedOrderAdapter(getActivity(), orderData.orders);
                listView.setAdapter(adapter);
            } else {
                noData.setVisibility(View.VISIBLE);
            }*/

        }
    }

    private class CompletedOrderAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        List<OrderModel> orders;

        public CompletedOrderAdapter(Context context, List<OrderModel> orders) {
            this.context = context;
            this.orders = orders;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // TODO Auto-generated method stub
            final ViewHolder mHolder;
            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.order_row,
                        parent, false);
                mHolder = new ViewHolder();
                mHolder.orderid = (TextView) convertView
                        .findViewById(R.id.orderId);
                mHolder.retailer = (TextView) convertView
                        .findViewById(R.id.retailer);
                mHolder.orderdate = (TextView) convertView
                        .findViewById(R.id.orderDate);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.orderid.setText(orders.get(position).order.order_id);
            mHolder.retailer.setText(orders.get(position).order.retailor_name);
            mHolder.orderdate
                    .setText(orders.get(position).order.order_date);

            return convertView;
        }

        private class ViewHolder {
            TextView orderid, retailer, orderdate;
        }
    }

}
