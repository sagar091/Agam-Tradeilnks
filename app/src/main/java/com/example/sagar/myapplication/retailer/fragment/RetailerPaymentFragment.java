package com.example.sagar.myapplication.retailer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.example.sagar.myapplication.model.OrderClass;
import com.example.sagar.myapplication.model.PaymentHistory;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class RetailerPaymentFragment extends Fragment {

    View parentView;
    private ComplexPreferences complexPreferences;
    String retailerId;
    PaymentHistory paymentHistory;
    int paymentError;
    ProgressDialog pd;
    ListView paymentList;
    ListViewAdapter adapter;

    public static RetailerPaymentFragment newInstance(String param1, String param2) {
        RetailerPaymentFragment fragment = new RetailerPaymentFragment();

        return fragment;
    }

    public RetailerPaymentFragment() {
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
        parentView = inflater.inflate(R.layout.fragment_retailer_payment, container, false);

        init(parentView);

        new GetPaymentHistory().execute();
        return parentView;
    }

    private void init(View parentView) {
        paymentList = (ListView) parentView.findViewById(R.id.paymentList);

        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        UserProfile userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        retailerId = userProfile.user_id;
    }

    private class GetPaymentHistory extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "retailor_payment");
            map.put("retailor_id", "422");
            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("product_details res", obj.toString());
                paymentError = obj.getInt("error");
                if (paymentError == 0) {
                    paymentHistory = new GsonBuilder().create().fromJson(obj.toString(), PaymentHistory.class);
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
            if (paymentHistory != null) {
                adapter = new ListViewAdapter(getActivity(), paymentHistory.data);
                paymentList.setAdapter(adapter);
            }
        }
    }

    private class ListViewAdapter extends BaseAdapter {

        Context context;
        LayoutInflater mInflater;
        List<OrderClass> data;

        public ListViewAdapter(Context context, List<OrderClass> data) {
            this.context = context;
            this.data = data;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder mHolder;

            if (convertView == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.payment_order_item,
                        parent, false);
                mHolder = new ViewHolder();
                mHolder.orderid = (TextView) convertView
                        .findViewById(R.id.orderId);
                mHolder.paymentTotal = (TextView) convertView
                        .findViewById(R.id.pTotal);
                mHolder.paymentPending = (TextView) convertView
                        .findViewById(R.id.pPending);
                mHolder.orderdate = (TextView) convertView
                        .findViewById(R.id.orderDate);
                convertView.setTag(mHolder);

            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.orderid.setText(data.get(position).order_id);
            mHolder.paymentTotal.setText(getResources().getString(R.string.Rs) + " " + data.get(position).order_total);
            mHolder.paymentPending.setText(getResources().getString(R.string.Rs) + " " + data.get(position).payment_recived);
            mHolder.orderdate.setText(data.get(position).date + " " + data.get(position).time);

            return null;
        }

        private class ViewHolder {
            TextView orderid, paymentTotal, paymentPending, orderdate;
        }
    }
}
