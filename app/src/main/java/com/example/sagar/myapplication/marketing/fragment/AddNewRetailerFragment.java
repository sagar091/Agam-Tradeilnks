package com.example.sagar.myapplication.marketing.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;

public class AddNewRetailerFragment extends Fragment {

    View parentView;
    RadioGroup radioGroup;
    LinearLayout linearAddress;
    int radioCheckedId = 0;

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
        parentView = inflater.inflate(R.layout.fragment_add_new_retailer, container, false);

        init(parentView);

        radioCheckedId = R.id.radioCurrent;

        return parentView;
    }

    private void init(View parentView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Add New Retailer");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");

        findViewById(parentView);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioCheckedId = checkedId;
                if (radioCheckedId == R.id.radioCurrent) {
                    linearAddress.setVisibility(View.GONE);
                } else if (radioCheckedId == R.id.radioAddress) {
                    linearAddress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void findViewById(View parentView) {
        radioGroup = (RadioGroup) parentView.findViewById(R.id.radioGroup);
        linearAddress = (LinearLayout) parentView.findViewById(R.id.linearAddress);

    }

}
