package com.example.sagar.myapplication.marketing.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.model.RetailerData;

import java.util.ArrayList;


public class RetailerMarketingFragment extends android.support.v4.app.Fragment {

    View customView;
    View parentView;
    RetailerData finalRetailerData;
    private String selectRetailerId, selectRetailerName;
    ListView offlineListView;
    SearchView searchView;
    private ComplexPreferences complexPreferences;

    public static RetailerMarketingFragment newInstance(String param1, String param2) {
        RetailerMarketingFragment fragment = new RetailerMarketingFragment();
        return fragment;
    }

    public RetailerMarketingFragment() {
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
        parentView = (View) customView.findViewById(android.R.id.content);

        offlineListView = (ListView) customView.findViewById(R.id.offlineList);
        searchView = (SearchView) customView.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    offlineListView.clearTextFilter();
                } else {
                    offlineListView.setFilterText(newText);
                }

                return true;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Search Retailer");

        finalRetailerData = new RetailerData();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        finalRetailerData = complexPreferences.getObject("offline_retailers", RetailerData.class);


        ArrayList<String> retailers = new ArrayList<>();
        for (int i = 0; i < finalRetailerData.retailers.size(); i++) {
            retailers.add(finalRetailerData.retailers.get(i).retailer.retailerName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, retailers);

        offlineListView.setAdapter(adapter);
        offlineListView.setTextFilterEnabled(true);

        offlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRetailerId = finalRetailerData.retailers.get(position).retailer.retailerId;
                selectRetailerName = finalRetailerData.retailers.get(position).retailer.retailerName;
                Log.e(selectRetailerId, selectRetailerName);

            }
        });

    }

}
