package com.example.sagar.myapplication.marketing.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.RetailerData;

import java.util.ArrayList;

public class PaymentFragment extends Fragment {

    View customView;
    View parentView;
    RetailerData finalRetailerData;
    private String selectRetailerId, selectRetailerName;
    ListView offlineListView;
    EditText edtSearch;
    private ComplexPreferences complexPreferences;

    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    public PaymentFragment() {
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
        customView = View.inflate(getActivity(), R.layout.offline_retailor_list, null);
        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Payment");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("Select Retailer");

        parentView = (View) customView.findViewById(android.R.id.content);

        edtSearch = (EditText) customView.findViewById(R.id.edtSearch);
        offlineListView = (ListView) customView.findViewById(R.id.offlineList);

        finalRetailerData = new RetailerData();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        finalRetailerData = complexPreferences.getObject("offline_retailers", RetailerData.class);

        ArrayList<String> retailers = new ArrayList<>();
        for (int i = 0; i < finalRetailerData.retailers.size(); i++) {
            retailers.add(finalRetailerData.retailers.get(i).retailer.retailerName);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, retailers);

        offlineListView.setAdapter(adapter);
        offlineListView.setTextFilterEnabled(true);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    adapter.notifyDataSetChanged();
                    adapter.getFilter().filter(s.toString().trim());
                }

                if (s.toString().length() == 0) {
                    adapter.getFilter().filter("");
                    offlineListView.clearTextFilter();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        offlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectRetailerName = parent.getItemAtPosition(position) + "";
                for (int i = 0; i < finalRetailerData.retailers.size(); i++) {
                    if (finalRetailerData.retailers.get(i).retailer.retailerName.equals(selectRetailerName)) {
                        selectRetailerId = finalRetailerData.retailers.get(i).retailer.retailerId;
                        break;
                    }
                }
                Log.e(selectRetailerId, selectRetailerName);

            }
        });
    }

}
