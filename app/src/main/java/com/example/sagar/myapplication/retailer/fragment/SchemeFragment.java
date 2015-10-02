package com.example.sagar.myapplication.retailer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;


public class SchemeFragment extends Fragment {

    View customView;

    public static SchemeFragment newInstance(String param1, String param2) {
        SchemeFragment fragment = new SchemeFragment();
        return fragment;
    }

    public SchemeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.fragment_scheme, container, false);

        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((RetailerDrawerActivity) getActivity()).setTitle("Schemes");
        ((RetailerDrawerActivity) getActivity()).setSubtitle("no");

    }

}
