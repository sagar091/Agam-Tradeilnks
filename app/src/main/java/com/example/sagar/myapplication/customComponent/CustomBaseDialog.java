package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class CustomBaseDialog extends BaseDialog {

    ArrayList<String> productDetails;
    TextView unitPrice, unitQty, unitTotalPrice;
    SeekBar seekbar;

    public CustomBaseDialog(Context context, ArrayList<String> productDetails) {
        super(context);
        this.productDetails = productDetails;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        View customView = View.inflate(context, R.layout.add_cart_dialog, null);
        unitPrice = (TextView) customView.findViewById(R.id.unitPrice);
        unitQty = (TextView) customView.findViewById(R.id.unitQty);
        unitTotalPrice = (TextView) customView.findViewById(R.id.unitTotalPrice);
        seekbar = (SeekBar) customView.findViewById(R.id.seekbar);

        unitPrice.setText(context.getResources().getString(R.string.Rs) + " " + productDetails.get(2));
        unitQty.setText("x 1 Qty");
        unitTotalPrice.setText("= " + context.getResources().getString(R.string.Rs) + " " + productDetails.get(2));

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return customView;
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
