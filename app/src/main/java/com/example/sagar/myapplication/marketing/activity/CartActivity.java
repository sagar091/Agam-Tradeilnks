package com.example.sagar.myapplication.marketing.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.model.ProductCart;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    DatabaseHandler handler;
    List<ProductCart> products = new ArrayList<>();
    private Toolbar toolbar;
    private ImageView imgCart;
    private TextView emptyCart;
    private ListView productsListView;
    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        products = new ArrayList<>();
        handler = new DatabaseHandler(CartActivity.this);
        products = handler.getProducts();

        Log.e("products", products.size() + "--");
        if (products.size() == 0) {
            emptyCart.setVisibility(View.VISIBLE);
        } else {
            emptyCart.setVisibility(View.GONE);
            adapter = new CardAdapter(this, products);
            productsListView.setAdapter(adapter);
        }

    }

    private void init() {
        productsListView = (ListView) findViewById(R.id.productsListView);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        if (toolbar != null) {
            toolbar.setTitle("Your Cart");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgCart.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private class CardAdapter extends BaseAdapter {

        Context context;
        List<ProductCart> products;
        LayoutInflater mInflater;

        public CardAdapter(Context context, List<ProductCart> products) {
            this.context = context;
            this.products = products;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cart_item, parent,
                        false);
                mHolder = new ViewHolder();
                mHolder.proName = (TextView) convertView
                        .findViewById(R.id.pName);
                mHolder.proPrice = (TextView) convertView
                        .findViewById(R.id.pPrice);
                mHolder.proColors = (TextView) convertView
                        .findViewById(R.id.colors);
                mHolder.proQty = (TextView) convertView.findViewById(R.id.qua);
                mHolder.remove = (ImageView) convertView
                        .findViewById(R.id.remove);
                mHolder.edit = (ImageView) convertView.findViewById(R.id.qty);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.proName.setText("Name: " + products.get(position).getName());
            mHolder.proPrice.setText("Price: "
                    + getResources().getString(R.string.Rs)
                    + products.get(position).getPrice());
            mHolder.proQty.setText(products.get(position).getQty());
            mHolder.proColors.setText(products.get(position).getColors());

            return convertView;
        }

        private class ViewHolder {
            TextView proName, proPrice, proColors;
            TextView proQty;
            ImageView remove;
            ImageView edit;
        }
    }
}
