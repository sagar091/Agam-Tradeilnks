package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.DatabaseHandler;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.model.Scheme;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class UpdateCartDialog extends BaseDialog {

    ArrayList<String> productDetails;
    ArrayList<String> cartProductDetails;
    TextView unitPrice, unitQty, unitTotalPrice;
    SeekBar seekbar;
    Button btnOK, btnCancel;
    private TagGroup mTagGroup;
    DatabaseHandler handler;
    StringBuilder sb;
    View customView;
    View parentView;
    List<Scheme> schemes;
    int qty;

    String productId, quantity, price;
    List<String> diffColors;


    public UpdateCartDialog(Context context, String productId, List<String> diffColors, String quantity, String price) {
        super(context);
        this.context = context;
        this.productId = productId;
        this.diffColors = diffColors;
        this.quantity = quantity;
        this.price = price;
    }

    public void setOnCartAddListener(OnCartAddListener onCartAddListener) {
        this.onCartAddListener = onCartAddListener;
    }

    OnCartAddListener onCartAddListener;

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        customView = View.inflate(context, R.layout.add_cart_dialog, null);
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
                    Functions.showSnack(customView, "Invalid Quantity");
                } else if (mTagGroup.getTags().length == 0) {
                    Functions.showSnack(customView, "Add atleast one color for model");
                } else {

                    sb = new StringBuilder();
                    for (int i = 0; i < mTagGroup.getTags().length; i++) {
                        Log.e("colors", mTagGroup.getTags()[i]);
                        sb.append(mTagGroup.getTags()[i] + ", ");
                    }

                    cartProductDetails.add(productId); // id
                    cartProductDetails.add(productDetails.get(1)); // name
                    cartProductDetails.add(productDetails.get(2)); // price
                    if (qty == 0) {
                        qty = 1;
                    }
                    cartProductDetails.add(qty + ""); // qty

                    String colors = sb.toString().substring(0, sb.toString().length() - 2);
                    cartProductDetails.add(colors); // colors

                    try {
                        handler.openDataBase();
                        boolean save = handler.addCartProduct(cartProductDetails, schemes);
                        if (save) {
                            if (onCartAddListener != null) {
                                onCartAddListener.onOkClick();
                            }
                            dismiss();
                        } else {
//                            Functions.showSnack(parentView,"Something went wrong");
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
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
        parentView = (View) findViewById(android.R.id.content);

        btnOK = (Button) customView.findViewById(R.id.btnOK);
        btnCancel = (Button) customView.findViewById(R.id.btnCancel);
        unitPrice = (TextView) customView.findViewById(R.id.unitPrice);
        unitQty = (TextView) customView.findViewById(R.id.unitQty);
        unitTotalPrice = (TextView) customView.findViewById(R.id.unitTotalPrice);

        seekbar = (SeekBar) customView.findViewById(R.id.seekbar);
        seekbar.setProgress(Integer.parseInt(quantity));

        mTagGroup = (TagGroup) customView.findViewById(R.id.tag_group);
        mTagGroup.setTags(diffColors);

        unitPrice.setText(context.getResources().getString(R.string.Rs) + " " + price);
        unitQty.setText("x " + quantity + " Qty");

        int q = Integer.parseInt(quantity);
        int t = Integer.parseInt(price);
        unitTotalPrice.setText("= " + context.getResources().getString(R.string.Rs) + (q * t));
    }

    @Override
    public boolean setUiBeforShow() {

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qty = progress;
                unitQty.setText("x " + progress + " Qty");
                unitTotalPrice.setText("= " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(Integer.parseInt(price) * progress));
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

    public interface OnCartAddListener {
        void onOkClick();
    }

}
