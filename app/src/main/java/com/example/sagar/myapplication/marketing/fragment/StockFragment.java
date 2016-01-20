package com.example.sagar.myapplication.marketing.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;


public class StockFragment extends Fragment {

    View customView;

    public static StockFragment newInstance(String param1, String param2) {
        StockFragment fragment = new StockFragment();
        return fragment;
    }

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.fragment_stock, container, false);

        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Stock");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");
        ((MarketingDrawerActivity) getActivity()).setFilterImage(false);

    }

}
