package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.CheckInDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.NearByUsers;
import com.example.sagar.myapplication.model.UserProfile;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;

import org.json.JSONObject;

import java.util.HashMap;

public class AgentFragment extends Fragment {

    View customView;
    SharedPreferences preferences;
    private EditText edtFeedback;
    private CheckBox cbPoster, cbBanner, cbDummy, cbEtc;
    private String isPoster, isBanner, isDummy, isEtc, strFeedback, retailerId, userId;
    int error;
    private Button btnSend, btnReset;
    ComplexPreferences complexPreferences;
    private ProgressDialog pd;

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

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.user_id;

        preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if (preferences.contains("offline")) {
            Log.e("offline", preferences.getString("offline", null));
            retailerId = preferences.getString("offline", null);
            Log.e("retailer_type", preferences.getString("retailer_type", null));

        } else {

            CheckInDialog dialog = new CheckInDialog(getActivity());
            dialog.setOnCancelListener(new CheckInDialog.onCancelListener() {
                @Override
                public void setCancel() {
                    Intent i = new Intent(getActivity(),
                            MarketingDrawerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            dialog.show();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Functions.getLength(edtFeedback) == 0) {
                    Functions.showSnack(customView, "Write something in feedback area to send feedback");
                } else {
                    saveFeedback();
                    new SendFeedback().execute();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        return customView;

    }

    private void saveFeedback() {
        strFeedback = Functions.getText(edtFeedback);
        if (cbPoster.isChecked())
            isPoster = "1";
        else
            isPoster = "0";
        if (cbBanner.isChecked())
            isBanner = "1";
        else
            isBanner = "0";
        if (cbEtc.isChecked())
            isEtc = "1";
        else
            isEtc = "0";
        if (cbDummy.isChecked())
            isDummy = "1";
        else
            isDummy = "0";
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Agents Activity");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");
        ((MarketingDrawerActivity) getActivity()).setFilterImage(false);

        edtFeedback = (EditText) customView.findViewById(R.id.edtFeedback);
        cbPoster = (CheckBox) customView.findViewById(R.id.cbPoster);
        cbBanner = (CheckBox) customView.findViewById(R.id.cbBanner);
        cbDummy = (CheckBox) customView.findViewById(R.id.cbDummy);
        cbEtc = (CheckBox) customView.findViewById(R.id.cbEtc);

        btnSend = (Button) customView.findViewById(R.id.btnSend);
        btnReset = (Button) customView.findViewById(R.id.btnReset);
    }

    private class SendFeedback extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "update_retailor_order_agent_activity");
            map.put("retailor_id", retailerId);
            map.put("user_id", userId);
            map.put("feedback", strFeedback);
            map.put("porters", isPoster);
            map.put("banners", isBanner);
            map.put("dummy_mobile", isDummy);
            map.put("extra_things", isEtc);
            map.put("order_id", "");

            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("agent_response", obj.toString());

                error = obj.getInt("error");
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (error == 0) {
                Functions.showSnack(customView, "Feedback sent successfully.");
                clearAll();
            } else {
                Functions.showSnack(customView, "Feedback can't send. Something went wrong..");
            }

        }
    }

    private void clearAll() {
        edtFeedback.setText("");
        cbDummy.setChecked(false);
        cbEtc.setChecked(false);
        cbBanner.setChecked(false);
        cbPoster.setChecked(false);
    }
}
