package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.model.UserProfile;

import java.util.HashMap;

public class OrderPendingFragment extends Fragment {

    private ListView listView;
    View parentView;
    ComplexPreferences complexPreferences;
    private String userId;
    ProgressDialog pd;

    public static OrderPendingFragment newInstance() {
        OrderPendingFragment fragment = new OrderPendingFragment();

        return fragment;
    }

    public OrderPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_order_pending, container, false);
        init(parentView);

        return parentView;

    }

    private void init(View parentView) {
        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        new GetPendingOrder().execute();

    }

    private class GetPendingOrder extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Fetching Pending Orders.", false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_orders");
            map.put("user_id", userId);
            try{

            }catch (Exception e){
                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
        }
    }
}
