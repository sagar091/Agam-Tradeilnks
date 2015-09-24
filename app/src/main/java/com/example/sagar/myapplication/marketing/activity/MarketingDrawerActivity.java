package com.example.sagar.myapplication.marketing.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.marketing.fragment.AddNewRetailerFragment;
import com.example.sagar.myapplication.marketing.fragment.AgentFragment;
import com.example.sagar.myapplication.marketing.fragment.DownloadSheetFragment;
import com.example.sagar.myapplication.marketing.fragment.HomeMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.MultipleOrdersPaymentFragment;
import com.example.sagar.myapplication.marketing.fragment.OrderMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.PaymentFragment;
import com.example.sagar.myapplication.marketing.fragment.RetailerMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.StockFragment;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.ui.CheckInActivity;
import com.example.sagar.myapplication.ui.MainActivity;

public class MarketingDrawerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private ImageView imgCart;
    SharedPreferences preferences;
    private ComplexPreferences complexPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_drawer);

        init();

        initDrawer();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, new HomeMarketingFragment());
        // ft.addToBackStack(null);
        ft.commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                setDrawerClick(menuItem.getItemId());

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setDrawerClick(int itemId) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        switch (itemId) {
            case R.id.drawer_home:
                ft.replace(R.id.content, new HomeMarketingFragment(), "Home");
                ft.commit();
                break;

            case R.id.drawer_retailer:
                ft.replace(R.id.content, new RetailerMarketingFragment(), "Retailers");
                ft.commit();
                break;

            case R.id.drawer_stock:
                ft.replace(R.id.content, new StockFragment(), "Stock");
                ft.commit();
                break;

            case R.id.drawer_orders:
                ft.replace(R.id.content, new OrderMarketingFragment(), "Orders");
                ft.commit();
                break;

            case R.id.drawer_add:
                ft.replace(R.id.content, new AddNewRetailerFragment(), "Add New Retailer");
                ft.commit();
                break;

            case R.id.drawer_payment:
                ft.replace(R.id.content, new PaymentFragment(), "Payment");
                ft.commit();
                break;

            case R.id.drawer_multi_payment:
                ft.replace(R.id.content, new MultipleOrdersPaymentFragment(), "Multiple Orders Payment");
                ft.commit();
                break;

            case R.id.drawer_check_in:
                Functions.fireIntent(MarketingDrawerActivity.this, CheckInActivity.class);
                break;

            case R.id.drawer_check_out:
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("offline");
                editor.commit();

                Intent intent = getIntent();
                finish();
                startActivity(intent);

                break;

            case R.id.drawer_agent:
                ft.replace(R.id.content, new AgentFragment(), "Agents Activity");
               /* preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                if (!preferences.contains("offline")) {
                    ft.addToBackStack(null);
                }*/
                ft.commit();
                break;

            case R.id.drawer_download:
                ft.replace(R.id.content, new DownloadSheetFragment(), "Download Sheet");
                ft.commit();
                break;

            case R.id.drawer_password:

                break;

            case R.id.drawer_log_out:
                complexPreferences = ComplexPreferences.getComplexPreferences(MarketingDrawerActivity.this, "user_pref", 0);
                UserProfile blankUser = new UserProfile();
                complexPreferences.putObject("current-user", blankUser);
                complexPreferences.commit();

                SharedPreferences preferences2 = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences2.edit();
                editor2.remove("isUserLogin");
                editor2.commit();

                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                break;


        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setSubtitle(String subtitle) {
        if (subtitle.equalsIgnoreCase("no")) {
            toolbar.setSubtitle(null);
        } else {
            toolbar.setSubtitle(subtitle);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Functions.closeKeyPad(MarketingDrawerActivity.this, drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Functions.closeKeyPad(MarketingDrawerActivity.this, drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // Set CheckIn CheckOut option visibility
        preferences = getSharedPreferences("login", MODE_PRIVATE);
        if (preferences.contains("offline")) {
            Log.e("offline", preferences.getString("offline", null));
            MenuItem item = navigationView.getMenu().getItem(7);
            item.setVisible(false);
        } else {
            Log.e("offline", "blank");
            MenuItem item = navigationView.getMenu().getItem(8);
            item.setVisible(false);
        }

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (toolbar != null) {
            toolbar.setTitle("Agam Tradelinks");
            setSupportActionBar(toolbar);
        }

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(MarketingDrawerActivity.this, CartActivity.class);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
