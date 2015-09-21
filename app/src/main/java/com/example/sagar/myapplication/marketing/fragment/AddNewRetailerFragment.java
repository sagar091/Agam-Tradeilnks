package com.example.sagar.myapplication.marketing.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;

public class AddNewRetailerFragment extends Fragment {

    View customView;

    public static AddNewRetailerFragment newInstance(String param1, String param2) {
        AddNewRetailerFragment fragment = new AddNewRetailerFragment();
        return fragment;
    }

    public AddNewRetailerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.fragment_add_new_retailer, container, false);

        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Add New Retailer");
    }

}
