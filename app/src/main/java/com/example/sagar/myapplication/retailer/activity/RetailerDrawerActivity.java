package com.example.sagar.myapplication.retailer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.SettingDialog;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.marketing.fragment.DownloadSheetFragment;
import com.example.sagar.myapplication.model.Retailer;
import com.example.sagar.myapplication.model.RetailerProfileModel;
import com.example.sagar.myapplication.model.UserProfile;
import com.example.sagar.myapplication.retailer.fragment.HomeRetailerFragment;
import com.example.sagar.myapplication.retailer.fragment.ProfileRetailerFragment;
import com.example.sagar.myapplication.retailer.fragment.RetailerPaymentFragment;
import com.example.sagar.myapplication.retailer.fragment.RetailersOrderFragment;
import com.example.sagar.myapplication.retailer.fragment.SchemeFragment;
import com.example.sagar.myapplication.ui.MainActivity;

public class RetailerDrawerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private ImageView imgCart;
    private ComplexPreferences complexPreferences;
    TextView txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_drawer);

        init();

        initDrawer();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, new HomeRetailerFragment());
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
                ft.replace(R.id.content, new HomeRetailerFragment(), "Home");
                ft.commit();
                break;

            case R.id.drawer_profile:
                ft.replace(R.id.content, new ProfileRetailerFragment(), "Profile");
                ft.commit();
                break;

            case R.id.drawer_orders:
                ft.replace(R.id.content, new RetailersOrderFragment(), "Orders");
                ft.commit();
                break;

            case R.id.drawer_payment:
                ft.replace(R.id.content, new RetailerPaymentFragment(), "Payment");
                ft.commit();
                break;

            case R.id.drawer_scheme:
                ft.replace(R.id.content, new SchemeFragment(), "Scheme");
                ft.commit();
                break;

            case R.id.drawer_download:
                ft.replace(R.id.content, new DownloadSheetFragment(), "Download Sheet");
                ft.commit();
                break;

            case R.id.drawer_log_out:
                Functions.closeSession(RetailerDrawerActivity.this);

                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;

        }
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Functions.hideKeyPad(RetailerDrawerActivity.this, drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Functions.hideKeyPad(RetailerDrawerActivity.this, drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // Set CheckIn CheckOut option visibility
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        UserProfile userProfile = new UserProfile();
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);

        txtUsername.setText(userProfile.name);
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

    private void init() {
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (toolbar != null) {
            toolbar.setTitle("Agam Tradelinks");
            setSupportActionBar(toolbar);
        }

        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.isConnecting(this)) {
            SettingDialog dialog = new SettingDialog(this, "You don't seem to have an active internet connection. Please check your internet connectivity and come again.", android.provider.Settings.ACTION_SETTINGS);
            dialog.setOnExitListener(new SettingDialog.OnExitListener() {
                @Override
                public void exit() {
                    finish();
                }
            });
            dialog.show();
        }
    }
}