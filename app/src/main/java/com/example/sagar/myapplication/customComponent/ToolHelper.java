package com.example.sagar.myapplication.customComponent;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.model.ProductCart;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class ToolHelper {

    Context _ctx;
    View view;
    ImageView imgCartMenu;
    BadgeView badge;
    List<ProductCart> products = new ArrayList<>();
    DatabaseHandler handler;

    public ToolHelper(Context _ctx, View view) {
        this._ctx = _ctx;
        this.view = view;
        imgCartMenu = (ImageView) view.findViewById(R.id.imgCart);
        badge = new BadgeView(_ctx, imgCartMenu);

    }

    public void displayBadge() {

        products = new ArrayList<>();
        handler = new DatabaseHandler(_ctx);
        products = handler.getProducts();

        Log.e("products", products.size() + "--");

        int value = products.size();

        if (value > 0) {
            imgCartMenu.setPadding(0, 18, 20, 0);
            badge.setText(String.valueOf(value));
            badge.setTextSize(16);
            badge.setBadgeBackgroundColor(_ctx.getResources().getColor(R.color.quad_orange));
            badge.show();
            ObjectAnimator animator = ObjectAnimator.ofFloat(badge, "rotationY", 0f, 360f);
            animator.setDuration(2500);
            animator.start();

        } else {
            imgCartMenu.setPadding(0, 0, 0, 0);
            badge.hide();
        }

    }

}
