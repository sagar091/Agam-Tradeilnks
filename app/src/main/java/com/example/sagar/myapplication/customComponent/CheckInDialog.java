package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.ui.CheckInActivity;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class CheckInDialog extends BaseDialog {

    View parentView;
    Button btnCheckIn, btnCancel;
    onCancelListener onCancelListener;

    public void setOnCancelListener(CheckInDialog.onCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public CheckInDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.check_in_dialog, null);
        init(parentView);

        return parentView;
    }

    private void init(View parentView) {
        btnCheckIn = (Button) parentView.findViewById(R.id.btnCheckIn);
        btnCancel = (Button) parentView.findViewById(R.id.btnCancel);

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,
                        CheckInActivity.class);
                context.startActivity(i);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onCancelListener != null) {
                    onCancelListener.setCancel();
                }
            }
        });
    }

    @Override
    public boolean setUiBeforShow() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        return false;
    }

    public interface onCancelListener {
        public void setCancel();
    }
}
