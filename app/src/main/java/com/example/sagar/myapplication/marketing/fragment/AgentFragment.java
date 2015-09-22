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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AgentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentFragment extends Fragment {

    View customView;

    public static AgentFragment newInstance(String param1, String param2) {
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

        return customView;

    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Agents Activity");
    }

}
