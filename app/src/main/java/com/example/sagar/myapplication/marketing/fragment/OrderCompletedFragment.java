package com.example.sagar.myapplication.marketing.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;


public class OrderCompletedFragment extends Fragment {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_completed, container, false);
    }

}
