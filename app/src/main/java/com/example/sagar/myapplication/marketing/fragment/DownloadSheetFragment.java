package com.example.sagar.myapplication.marketing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.model.CompanyData;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadSheetFragment extends Fragment {

    View customView;
    private EditText edtCompany;
    CompanyData companyData;
    ProgressDialog pd;
    private String selectCompanyId, selectCompanyName, sheetError, sheetURL;
    private ComplexPreferences complexPreferences;
    LinearLayout linearButtons;
    WebView webView;
    Button btnView, btnDownload;
    boolean download = false;
    String type = null;
    String filename;
    File SDCardRoot, dir;

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

        SDCardRoot = Environment.getExternalStorageDirectory();
        dir = new File(SDCardRoot.getAbsolutePath()
                + "/Agam Downloads");

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

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + sheetURL);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (download) {
                    Functions.snack(customView, "File already downloaded in Internal Storage -> Agam Downloads folder.");
                } else {
                    new DownloadSheet().execute();
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
                download = false;
                btnDownload.setText("Download");
                selectCompanyId = companyData.company.get(position).cat_id;
                selectCompanyName = companyData.company.get(position).cat_name;
                edtCompany.setText(selectCompanyName);
                dialog.dismiss();
                new CountDownTimer(900, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        webView.setVisibility(View.GONE);
                        new GetSheetLink().execute(selectCompanyId);
                    }
                }.start();
            }
        });
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Download Sheet");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");

        btnDownload = (Button) customView.findViewById(R.id.btnDownload);
        btnView = (Button) customView.findViewById(R.id.btnView);
        webView = (WebView) customView.findViewById(R.id.webView);
        edtCompany = (EditText) customView.findViewById(R.id.edtCompany);
        linearButtons = (LinearLayout) customView.findViewById(R.id.linearButtons);

        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

    }

    private class GetSheetLink extends AsyncTask<String, Void, String> {

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

            try {
                HttpRequest req = new HttpRequest(Constants.BASE_URL);
                JSONObject obj = req.preparePost().withData(map).sendAndReadJSON();
                Log.e("sheet_response", obj.toString());
                JSONObject statusObj = obj.getJSONObject("status");
                sheetError = statusObj.getString("error");
                if (sheetError.equals("0")) {
                    sheetURL = statusObj.getString("url");
                } else {
                    Functions.snack(customView, "Cannot find Sheet.");
                }
            } catch (Exception e) {
                Functions.snack(customView, "Error. " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            linearButtons.setVisibility(View.VISIBLE);
        }
    }

    private class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(sheetURL)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    private class DownloadSheet extends AsyncTask<String, String, String> {

        String url = sheetURL;
        String contentDisposition = "attachment";

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getMimeTypeFromExtension(extension);
            }
            filename = URLUtil.guessFileName(url, contentDisposition, type);
            try {
                URL newURL = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) newURL
                        .openConnection();
                urlConnection.connect();

                dir.mkdir();
                File file = new File(dir, filename);

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;

                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    int progress = (int) (downloadedSize * 100 / totalSize);
                    publishProgress(progress + "");
                }
                fileOutput.close();
                download = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Functions.snack(customView, "Your file stored in Internal Storage -> Agam Downloads folder.");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            btnDownload.setText(values[0] + "%");
            if (values[0].equals("100"))
                btnDownload.setText("Downloaded");

        }
    }
}
