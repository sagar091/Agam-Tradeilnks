package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.flyco.animation.Attention.Flash;
import com.flyco.animation.Attention.RubberBand;
import com.flyco.animation.Attention.Tada;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FallEnter.FallEnter;
import com.flyco.animation.Jelly;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class AskDialog extends BaseDialog {

    View parentView;
    Button btnYes, btnNo;
    OnYesClickListener onYesListener;
    String message;
    TextView txtMessage;

    public void setOnYesListener(AskDialog.OnYesClickListener onYesListener) {
        this.onYesListener = onYesListener;
    }

    public AskDialog(Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.ask_dialog, null);
        init(parentView);

        return parentView;
    }

    private void init(final View parentView) {
        txtMessage = (TextView) parentView.findViewById(R.id.txtMessage);
        btnYes = (Button) parentView.findViewById(R.id.btnYes);
        btnNo = (Button) parentView.findViewById(R.id.btnNo);

        txtMessage.setText(message);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYesListener != null) {
                    onYesListener.clickYes();
                }
                dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

    }

    @Override
    public boolean setUiBeforShow() {
        setCanceledOnTouchOutside(false);
        return false;
    }

    public interface OnYesClickListener {
        void clickYes();
    }

}
