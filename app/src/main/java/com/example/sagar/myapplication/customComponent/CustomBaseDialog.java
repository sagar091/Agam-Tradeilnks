package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.helper.Functions;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class CustomBaseDialog extends BaseDialog {

    ArrayList<String> productDetails;
    ArrayList<String> cartProductDetails;
    TextView unitPrice, unitQty, unitTotalPrice;
    SeekBar seekbar;
    Button btnOK, btnCancel;
    private TagGroup mTagGroup;
    DatabaseHandler handler;
    StringBuilder sb;

    public CustomBaseDialog(Context context, ArrayList<String> productDetails) {
        super(context);
        this.productDetails = productDetails;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        View customView = View.inflate(context, R.layout.add_cart_dialog, null);
        init(customView);

        handler = new DatabaseHandler(context);

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProductDetails = new ArrayList<String>();
                if (seekbar.getProgress() == 0) {
                    Functions.snack(v, "Invalid Quantity");
                } else if (mTagGroup.getTags().length == 0) {
                    Functions.snack(v, "Add atleast one color for model");
                } else {
                    Log.e("productDetails", productDetails.toString());
                    Log.e("qty", seekbar.getProgress() + " Qty");
                    sb = new StringBuilder();
                    for (int i = 0; i < mTagGroup.getTags().length; i++) {
                        Log.e("colors", mTagGroup.getTags()[i]);
                        sb.append(mTagGroup.getTags()[i]);
                    }

                    cartProductDetails.add(productDetails.get(1));
                    cartProductDetails.add(seekbar.getProgress() + "");
                    cartProductDetails.add(sb.toString());

                    try {
                        handler.openDataBase();
                        boolean save = handler.addCartProduct(cartProductDetails);
                        if (save) {
                            dismiss();
                            Functions.snack(v, "Product added successfully");
                        } else {
                            Functions.snack(v, "Something went wrong");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        return customView;
    }

    private void init(View customView) {
        mTagGroup = (TagGroup) customView.findViewById(R.id.tag_group);
        btnOK = (Button) customView.findViewById(R.id.btnOK);
        btnCancel = (Button) customView.findViewById(R.id.btnCancel);
        unitPrice = (TextView) customView.findViewById(R.id.unitPrice);
        unitQty = (TextView) customView.findViewById(R.id.unitQty);
        unitTotalPrice = (TextView) customView.findViewById(R.id.unitTotalPrice);
        seekbar = (SeekBar) customView.findViewById(R.id.seekbar);

        unitPrice.setText(context.getResources().getString(R.string.Rs) + " " + productDetails.get(2));
        unitQty.setText("x 1 Qty");
        unitTotalPrice.setText("= " + context.getResources().getString(R.string.Rs) + " " + productDetails.get(2));
    }

    @Override
    public boolean setUiBeforShow() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                unitQty.setText("x " + progress + " Qty");
                unitTotalPrice.setText("= " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(Integer.parseInt(productDetails.get(2)) * progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return false;
    }
}
