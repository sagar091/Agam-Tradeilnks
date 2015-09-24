package com.example.sagar.myapplication.marketing.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.CheckInDialog;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.ui.CheckInActivity;

public class AgentFragment extends Fragment {

    View customView;
    SharedPreferences preferences;

    public static AgentFragment newInstance() {
        AgentFragment fragment = new AgentFragment();
        return fragment;
    }

    public AgentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.fragment_agent, container, false);

        init(customView);

        preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if (preferences.contains("offline")) {
            Log.e("offline", preferences.getString("offline", null));
        } else {
            Log.e("offline", "blank");

            CheckInDialog dialog = new CheckInDialog(getActivity());
            dialog.setOnCancelListener(new CheckInDialog.onCancelListener() {
                @Override
                public void setCancel() {
                   Intent i = new Intent(getActivity(),
                            MarketingDrawerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                   // getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            dialog.show();
        }

        return customView;

    }

    private void CheckInAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setMessage("You have to check-in for feedback procedure..")
                .setCancelable(false)
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
        builder.setNegativeButton("Check In",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(),
                                CheckInActivity.class);
                        startActivity(i);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Agents Activity");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");
    }

}
