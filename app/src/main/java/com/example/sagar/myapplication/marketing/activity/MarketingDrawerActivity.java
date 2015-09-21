package com.example.sagar.myapplication.marketing.activity;

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
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.marketing.fragment.AddNewRetailerFragment;
import com.example.sagar.myapplication.marketing.fragment.HomeMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.OrderMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.RetailerMarketingFragment;
import com.example.sagar.myapplication.marketing.fragment.StockFragment;

public class MarketingDrawerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private ImageView imgCart;
    SharedPreferences preferences;

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
                break;

            case R.id.drawer_multi_payment:
                break;

            case R.id.drawer_check_in:
                break;

            case R.id.drawer_check_out:
                break;

            case R.id.drawer_agent:
                break;

            case R.id.drawer_download:
                break;

            case R.id.drawer_password:
                break;

            case R.id.drawer_log_out:
                break;


        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
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

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

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
                Functions.snack(v, "Cart");
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
