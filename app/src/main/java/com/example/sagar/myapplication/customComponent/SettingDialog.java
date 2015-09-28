package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class SettingDialog extends BaseDialog {

    View parentView;
    Button btnSetting, btnExit;
    OnExitListener onExitListener;
    String message;
    TextView txtMessage;
    String action;

    public void setOnExitListener(SettingDialog.OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
    }

    public SettingDialog(Context context, String message, String action) {
        super(context);
        this.message = message;
        this.action = action;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.setting_dialog, null);
        init(parentView);

        return parentView;
    }

    private void init(final View parentView) {
        txtMessage = (TextView) parentView.findViewById(R.id.txtMessage);
        btnSetting = (Button) parentView.findViewById(R.id.btnSetting);
        btnExit = (Button) parentView.findViewById(R.id.btnExit);

        txtMessage.setText(message);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(action));
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onExitListener != null) {
                    onExitListener.exit();
                }
            }
        });

    }

    @Override
    public boolean setUiBeforShow() {
        setCanceledOnTouchOutside(false);
        return false;
    }

    public interface OnExitListener {
        void exit();
    }

}
