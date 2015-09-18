package com.example.sagar.myapplication.customComponent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.example.sagar.myapplication.R;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class AddCartDialog extends Dialog {

    Context context;

    public AddCartDialog(Context context) {
        super(context);
    }

    public AddCartDialog(FragmentActivity context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.add_cart_dialog);

        init();
    }

    private void init() {

    }
}
