package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.content.Intent;
import android.view.View;

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

    public void setOnExitListener(SettingDialog.OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
    }

    public SettingDialog(Context context) {
        super(context);
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
        btnSetting = (Button) parentView.findViewById(R.id.btnSetting);
        btnExit = (Button) parentView.findViewById(R.id.btnExit);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
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
