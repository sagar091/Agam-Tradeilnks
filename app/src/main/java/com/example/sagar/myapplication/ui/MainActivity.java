package com.example.sagar.myapplication.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    SharedPreferences sp;
    LinearLayout displayLayout, osLayout, camLayout, ramLayout, expLayout,
            fmLayout, audioLayout, processorLayout, batteryLayout, btLayout,
            flashLayout, colorLayout, specialLayout;
    public static final String PREFERENFCE = "MyPref";
    public static final String name = "nameKey";
    public static final String pass = "passKey";
    EditText companyEdit, modelEdit;
    Button goButton;
    LinearLayout prodcutDetails;
    String a = "subcat_list", b = "product_list", c = "product_details";
    HttpURLConnection conn = null;
    ArrayAdapter<String> ad, ad1;
    String catId = "0", catName = "", mdId = "0", mdName = "";
    ArrayList<String> catList = new ArrayList<String>();
    ArrayList<String> catIds = new ArrayList<String>();
    ArrayList<String> modelList = new ArrayList<String>();
    ArrayList<String> modelIds = new ArrayList<String>();
    String data;
    BufferedReader reader = null;
    StringBuilder sb = new StringBuilder();
    int h, w;
    boolean m = false;
    String text;
    // Product Details
    ImageView smallImage;
    TextView pbrand, pname, pbattery, pcam, pfcam, pdisplay, pprice, pflesh,
            pprocessor, pos, prom, pColor, pBt, pAudio, pFm, pExp, pSpecial;
    String nameValue, imageValue, batteryValue, processorValue, displayValue,
            priceValue, ramRomValue, mrpValue, osValue, cameraValue, fmValue,
            flashValue, colorValue, btValue, audioValue, expValue,
            specialValue;
    Button addCart;
    Bitmap bitmap;
    ProgressDialog pDialog;

    ImageView imgAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fetchCompany();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        addCart.setVisibility(View.GONE);

        companyEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

               /* new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Company")
                        .setAdapter(ad, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                catName = catList.get(which).toString();
                                companyEdit.setText(catName);
                                catId = catIds.get(which).toString();
                                Log.i(catId, catName);
                                mdId = "";
                                mdName = "";
                                modelEdit.setHintTextColor(Color
                                        .parseColor("#ABABAB"));
                                modelEdit.setText("");
                                modelList.clear();
                                modelIds.clear();
                                new GetModel().execute(catId);
                                dialog.dismiss();
                            }
                        }).create().show();*/

            }
        });

        /*modelEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Model")
                        .setAdapter(ad1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mdName = modelList.get(which).toString();
                                mdId = modelIds.get(which).toString();
                                modelEdit.setText(mdName);
                                Log.i(mdId, mdName);
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });*/

        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final String[] stringItems = {"One", "Two", "Threee", "Four", "Five", "Six", "One", "Two", "Threee", "Four", "Five", "Six", "One", "Two", "Threee", "Four", "Five", "Six"};
                final ActionSheetDialog dialog = new ActionSheetDialog(MainActivity.this, stringItems, goButton);
                dialog.isTitleShow(false).show();

                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(MainActivity.this, stringItems[position] + "", Toast.LENGTH_SHORT).show();
                        companyEdit.setText(stringItems[position]);
                        dialog.dismiss();
                    }
                });


               /* if (!catId.equals("0") && !mdId.equals("0")) {
                    new GetProductDetails().execute(mdId);
                } else {
                    Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
                    // alert();
                }*/
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        h = metrics.heightPixels;
        w = metrics.widthPixels;

        smallImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!imageValue.equals("false")) {
                    prodcutDetails.setVisibility(View.GONE);
                    if (m == false) {
                        m = true;
                    }
                    //  new LoadImage().execute(imageValue);
                    // imageLoader.DisplayImage(a2, fullImageView);
                }
            }
        });

        catIds = new ArrayList<String>();
        catList = new ArrayList<String>();
        companyEdit.setText("");
        companyEdit.setHint("Select Company");
        modelEdit.setText("");
        modelEdit.setHint("Select Model");
        catId = "0";
        mdId = "0";
        // new GetCompany().execute(a);

    }

    private void fetchCompany() {
    }


    private class GetProductDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            prodcutDetails.setVisibility(View.GONE);

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                data = URLEncoder.encode("form_type", "UTF-8") + "="
                        + URLEncoder.encode("product_details", "UTF-8");
                data += "&" + URLEncoder.encode("product_id", "UTF-8") + "="
                        + URLEncoder.encode(params[0], "UTF-8");

                URL url = new URL(Constants.BASE_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                JSONObject obj = new JSONObject(sb.toString());
                JSONObject obj2 = obj.getJSONObject("products");
                nameValue = obj2.getString("name");
                imageValue = obj2.getString("product_image");
                JSONObject obj3 = obj2.getJSONObject("featured_details");
                displayValue = obj3.getString("screen_size");
                osValue = obj3.getString("os");
                cameraValue = obj3.getString("camera");
                ramRomValue = obj3.getString("ram_rom");
                expValue = obj3.getString("expandable_slot");
                fmValue = obj3.getString("fm");
                audioValue = obj3.getString("audio_video");
                processorValue = obj3.getString("processor");
                batteryValue = obj3.getString("battery");
                btValue = obj3.getString("bt");
                flashValue = obj3.getString("flash");
                priceValue = obj3.getString("mrp");
                mrpValue = obj3.getString("mrp");
                colorValue = obj3.getString("color");
                specialValue = obj3.getString("special_feature");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            prodcutDetails.setVisibility(View.VISIBLE);

            pbrand.setText(catName);
            pname.setText(nameValue);
            if (batteryValue.length() > 0) {
                batteryLayout.setVisibility(View.VISIBLE);
                pbattery.setText(batteryValue);
            } else {
                batteryLayout.setVisibility(View.GONE);
            }
            if (processorValue.length() > 0) {
                processorLayout.setVisibility(View.VISIBLE);
                pprocessor.setText(processorValue);
            } else {
                processorLayout.setVisibility(View.GONE);
            }
            if (displayValue.length() > 0) {
                displayLayout.setVisibility(View.VISIBLE);
                pdisplay.setText(displayValue);
            } else {
                displayLayout.setVisibility(View.GONE);
            }
            if (ramRomValue.length() > 0) {
                ramLayout.setVisibility(View.VISIBLE);
                prom.setText(ramRomValue);
            } else {
                ramLayout.setVisibility(View.GONE);
            }
            if (expValue.length() > 0) {
                expLayout.setVisibility(View.VISIBLE);
                pExp.setText(expValue);
            } else {
                expLayout.setVisibility(View.GONE);
            }
            pprice.setText(Html.fromHtml("<b>"
                    + "Rs." + "</b>" + " "
                    + mrpValue));
            if (osValue.length() > 0) {
                osLayout.setVisibility(View.VISIBLE);
                pos.setText(osValue);
            } else {
                osLayout.setVisibility(View.GONE);
            }
            if (cameraValue.length() > 0) {
                camLayout.setVisibility(View.VISIBLE);
                pcam.setText(cameraValue);
            } else {
                camLayout.setVisibility(View.GONE);
            }
            if (flashValue.length() > 0) {
                flashLayout.setVisibility(View.VISIBLE);
                pflesh.setText(flashValue);
            } else {
                flashLayout.setVisibility(View.GONE);
            }
            if (colorValue.length() > 0) {
                colorLayout.setVisibility(View.VISIBLE);
                pColor.setText(colorValue);
            } else {
                colorLayout.setVisibility(View.GONE);
            }
            if (btValue.length() > 0) {
                btLayout.setVisibility(View.VISIBLE);
                pBt.setText(btValue);
            } else {
                btLayout.setVisibility(View.GONE);
            }
            if (audioValue.length() > 0) {
                audioLayout.setVisibility(View.VISIBLE);
                pAudio.setText(audioValue);
            } else {
                audioLayout.setVisibility(View.GONE);
            }
            if (fmValue.length() > 0) {
                fmLayout.setVisibility(View.VISIBLE);
                pFm.setText(fmValue);
            } else {
                audioLayout.setVisibility(View.GONE);
            }
            if (specialValue.length() > 0) {
                specialLayout.setVisibility(View.VISIBLE);
                pSpecial.setText(specialValue);
            } else {
                specialLayout.setVisibility(View.GONE);
            }
        }
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgAccount = (ImageView) findViewById(R.id.imgAccount);

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.fireIntent(MainActivity.this, LoginActivity.class);
            }
        });

        companyEdit = (EditText) findViewById(R.id.companySpinner);
        modelEdit = (EditText) findViewById(R.id.modelSpinner);
        prodcutDetails = (LinearLayout) findViewById(R.id.scroll);
        goButton = (Button) findViewById(R.id.imgGo);

        displayLayout = (LinearLayout) findViewById(R.id.displayLayout);
        osLayout = (LinearLayout) findViewById(R.id.osLayout);
        camLayout = (LinearLayout) findViewById(R.id.pcamLayout);
        ramLayout = (LinearLayout) findViewById(R.id.ramLayout);
        expLayout = (LinearLayout) findViewById(R.id.expLayout);
        fmLayout = (LinearLayout) findViewById(R.id.fmLayout);
        audioLayout = (LinearLayout) findViewById(R.id.audioLayout);
        processorLayout = (LinearLayout) findViewById(R.id.processorLayout);
        batteryLayout = (LinearLayout) findViewById(R.id.batteryLayout);
        btLayout = (LinearLayout) findViewById(R.id.btLayout);
        flashLayout = (LinearLayout) findViewById(R.id.flashLayout);
        colorLayout = (LinearLayout) findViewById(R.id.colorLayout);
        specialLayout = (LinearLayout) findViewById(R.id.specialLayout);

        // Product Details
        pbrand = (TextView) findViewById(R.id.pBrand);
        pname = (TextView) findViewById(R.id.pName);
        pbattery = (TextView) findViewById(R.id.pBattery);
        pcam = (TextView) findViewById(R.id.pCam);
        pprocessor = (TextView) findViewById(R.id.pProcessor);
        pdisplay = (TextView) findViewById(R.id.pDisplay);
        pprice = (TextView) findViewById(R.id.pPrice);
        pflesh = (TextView) findViewById(R.id.pFlash);
        pos = (TextView) findViewById(R.id.pOS);
        prom = (TextView) findViewById(R.id.pROM);
        pExp = (TextView) findViewById(R.id.pExp);
        smallImage = (ImageView) findViewById(R.id.smallImage);
        addCart = (Button) findViewById(R.id.addCart);
        pColor = (TextView) findViewById(R.id.pColor);
        pBt = (TextView) findViewById(R.id.pBt);
        pAudio = (TextView) findViewById(R.id.pAudio);
        pFm = (TextView) findViewById(R.id.pFM);
        pSpecial = (TextView) findViewById(R.id.pSpecial);
    }

    private class GetModel extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mdId = "0";
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                data = URLEncoder.encode("form_type", "UTF-8") + "="
                        + URLEncoder.encode(b, "UTF-8");
                data += "&" + URLEncoder.encode("cat_id", "UTF-8") + "="
                        + URLEncoder.encode(params[0], "UTF-8");

                URL url = new URL(Constants.BASE_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray array = obj.getJSONArray("products");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj3 = array.getJSONObject(i);
                    modelList.add(obj3.getString("name"));
                    modelIds.add(obj3.getString("id"));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            modelEdit.setEnabled(true);
            modelEdit.setHintTextColor(Color.parseColor("#000000"));
            ad1 = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_single_choice, modelList);
        }
    }

    private class GetCompany extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            catId = "0";
            mdId = "0";
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                data = URLEncoder.encode("form_type", "UTF-8") + "="
                        + URLEncoder.encode("subcat_list", "UTF-8");

                URL url = new URL(Constants.BASE_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray array = obj.getJSONArray("cats");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj3 = array.getJSONObject(i);
                    catIds.add(obj3.getString("cat_id"));
                    catList.add(obj3.getString("cat_name"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            companyEdit.setHintTextColor(Color.parseColor("#000000"));
            ad = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_single_choice, catList);
        }

    }

}
