package com.example.sagar.myapplication.marketing.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.CompanyData;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadSheetFragment extends Fragment {

    View customView;
    private EditText edtCompany;
    CompanyData companyData;
    ProgressDialog pd;
    private String selectCompanyId;
    int modelError;
    private ComplexPreferences complexPreferences;
    LinearLayout linearButtons;

    public static DownloadSheetFragment newInstance(String param1, String param2) {
        DownloadSheetFragment fragment = new DownloadSheetFragment();

        return fragment;
    }

    public DownloadSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customView = inflater.inflate(R.layout.fragment_download_sheet, container, false);

        init(customView);

        companyData = new CompanyData();
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        companyData = complexPreferences.getObject("mobile_companies", CompanyData.class);

        edtCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (companyData.company.size() > 0) {
                    setCompanyDialog();
                } else {
                    Snackbar.make(edtCompany, "No data for company", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        return customView;
    }

    private void setCompanyDialog() {

        ArrayList<String> cats = new ArrayList<String>();
        for (int i = 0; i < companyData.company.size(); i++) {
            cats.add(companyData.company.get(i).cat_name);
        }

        String[] stringItems = new String[cats.size()];
        stringItems = cats.toArray(stringItems);

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, edtCompany);
        dialog.isTitleShow(true).show();
        dialog.title("Select Company").titleTextSize_SP(20);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCompanyId = companyData.company.get(position).cat_id;
                edtCompany.setText(companyData.company.get(position).cat_name);
                dialog.dismiss();
                new CountDownTimer(900, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        new GetDownloadLink().execute(selectCompanyId);
                    }
                }.start();
            }
        });
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Download Sheet");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");

        edtCompany = (EditText) customView.findViewById(R.id.edtCompany);
        linearButtons = (LinearLayout) customView.findViewById(R.id.linearButtons);

    }

    private class GetDownloadLink extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "Loading", "Please wait..", false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put("form_type", "get_sheet");
            map.put("cat_id", params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            linearButtons.setVisibility(View.VISIBLE);
        }
    }
}
