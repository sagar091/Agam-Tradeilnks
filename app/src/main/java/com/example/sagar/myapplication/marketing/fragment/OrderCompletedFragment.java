package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.HttpRequest;
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
            map.put("form_type", "get_completed_orders");
            map.put("user_id", userId);
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                //Log.e("order_pending_response", obj.toString());
                orderError = obj.getInt("error");
                if (orderError == 0) {
                    orderData = new GsonBuilder().create().fromJson(obj.toString(), OrderMarketingData.class);
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            if (orderData.orders.size() > 0) {
                adapter = new CompletedOrderAdapter(getActivity(), orderData.orders);
                listView.setAdapter(adapter);
            }

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
            mHolder.retailer.setText(orders.get(position).order.retailor_id);
            mHolder.orderdate
                    .setText(orders.get(position).order.order_date);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView orderid, retailer, orderdate;
        }
    }

}
