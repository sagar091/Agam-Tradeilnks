package com.example.sagar.myapplication.marketing.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.customComponent.AskDialog;
import com.example.sagar.myapplication.customComponent.SchemeViewDialog;
import com.example.sagar.myapplication.customComponent.UpdateCartDialog;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.model.ProductCart;
import com.example.sagar.myapplication.model.Scheme;
import com.rey.material.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    DatabaseHandler handler;
    private TextView txtTotal;
    View parentView;
    List<ProductCart> products = new ArrayList<>();
    private Toolbar toolbar;
    private ImageView imgCart;
    private TextView emptyCart;
    private ListView productsListView;
    private CardAdapter adapter;
    private RelativeLayout cartLayout;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        displayProducts();

    }

    private void displayProducts() {
        products = new ArrayList<>();
        handler = new DatabaseHandler(CartActivity.this);
        try {
            handler.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        products = handler.getProducts();

        Log.e("products", products.size() + "--");

        if (products.size() == 0) {
            emptyCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);

        } else {
            emptyCart.setVisibility(View.GONE);
            adapter = new CardAdapter(this, products);
            productsListView.setAdapter(adapter);
            cartLayout.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        parentView = findViewById(android.R.id.content);
        cartLayout = (RelativeLayout) findViewById(R.id.cartLayout);
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cart_item, parent,
                        false);
                mHolder = new ViewHolder();
                mHolder.txtProductName = (TextView) convertView
                        .findViewById(R.id.txtProductName);
                mHolder.txtProductPrice = (TextView) convertView
                        .findViewById(R.id.txtProductPrice);
                mHolder.txtColors = (TextView) convertView
                        .findViewById(R.id.txtColors);
                mHolder.txtQty = (TextView) convertView.findViewById(R.id.txtQty);
                mHolder.txtSelectedScheme = (TextView) convertView.findViewById(R.id.txtSelectedScheme);
                mHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
                mHolder.btnScheme = (Button) convertView.findViewById(R.id.btnScheme);
                mHolder.edit = (ImageView) convertView.findViewById(R.id.qty);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            String str = "<b>Name: </b>" + products.get(position).getName();
            mHolder.txtProductName.setText(Html.fromHtml(str));

            str = "<b>Price: </b>" + getResources().getString(R.string.Rs)
                    + products.get(position).getPrice();
            mHolder.txtProductPrice.setText(Html.fromHtml(str));

            mHolder.txtQty.setText(products.get(position).getQty());

            total += Integer.parseInt(products.get(position).getPrice()) * Integer.parseInt(products.get(position).getQty());

            str = "<b>Colors: </b>" + products.get(position).getColors();
            mHolder.txtColors.setText(Html.fromHtml(str));

            if (products.get(position).getSchemeText() == null) {
                str = "<b>Sheme: </b>" + "No scheme";
            } else {
                str = "<b>Scheme: </b>" + products.get(position).getSchemeText();
            }
            mHolder.txtSelectedScheme.setText(Html.fromHtml(str));

            mHolder.btnScheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler = new DatabaseHandler(context);
                    List<Scheme> schemes = handler.getScheme(products.get(position).getProductId());

                    SchemeViewDialog dialog = new SchemeViewDialog(context, schemes, "cart");
                    dialog.setOnApplyListener(new SchemeViewDialog.OnApplyListener() {
                        @Override
                        public void onApplyClick(int schemeId, String scheme) {
                            handler = new DatabaseHandler(context);
                            handler.addScheme(products.get(position).getProductId(), schemeId, scheme);
                            displayProducts();
                        }

                    });
                    dialog.show();
                }
            });

            mHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = products.get(position).getProductId();
                    String colors = products.get(position).getColors();
                    String qty = products.get(position).getQty();
                    String price = products.get(position).getPrice();

                    String[] diff = colors.split(", ");
                    List<String> diffColors = new ArrayList<String>();
                    for (int k = 0; k < diff.length; k++) {
                        diffColors.add(diff[k]);
                    }

                    UpdateCartDialog updateCartDialog = new UpdateCartDialog(context, productId, diffColors, qty, price);
                    updateCartDialog.setOnCartAddListener(new UpdateCartDialog.OnCartAddListener() {
                        @Override
                        public void onOkClick() {
                            Functions.showSnack(parentView, "Product updated");
                            displayProducts();

                        }
                    });
                    updateCartDialog.show();
                }
            });

            mHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AskDialog askDialog = new AskDialog(context, "Do you want to remove this product from cart?");
                    askDialog.setOnYesListener(new AskDialog.OnYesClickListener() {
                        @Override
                        public void clickYes() {
                            handler = new DatabaseHandler(context);
                            handler.deleteCartProduct(products.get(position).getProductId());
                            displayProducts();
                        }
                    });
                    askDialog.show();
                }
            });

            txtTotal.setText("Total: " + getResources().getString(R.string.Rs) + " " + total);
            return convertView;
        }

        private class ViewHolder {
            TextView txtProductName, txtProductPrice, txtColors, txtSelectedScheme;
            TextView txtQty;
            ImageView remove;
            ImageView edit;
            Button btnScheme;
        }
    }
}
