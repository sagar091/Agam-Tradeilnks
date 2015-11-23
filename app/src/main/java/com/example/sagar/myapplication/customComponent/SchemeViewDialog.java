package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.model.Scheme;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

import java.util.List;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class SchemeViewDialog extends BaseDialog {

    View parentView;
    private LinearLayout viewLayout, selectLayout, schemeLayout, mainLayout;
    private TextView txtNote, txtNoScheme;
    List<Scheme> schemes;
    Button btnApply;
    String type, schemeText;
    RadioGroup rGroup;
    int selectedSchemeId = -1;
    OnApplyListener onApplyListener;

    public void setOnApplyListener(OnApplyListener onApplyListener) {
        this.onApplyListener = onApplyListener;
    }

    public SchemeViewDialog(Context context, List<Scheme> schemes, String type) {
        super(context);
        this.schemes = schemes;
        this.type = type;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.scheme_view, null);
        init(parentView);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSchemeId == -1) {
                    Functions.showSnack(parentView, "Select any scheme or Select no scheme");
                } else {
                    if (onApplyListener != null) {
                        onApplyListener.onApplyClick(selectedSchemeId, schemeText);
                    }
                    //Functions.showSnack(parentView, "Selected scheme " + selectedSchemeId);
                    dismiss();
                }

            }
        });

        if (schemes.size() == 0) {
            txtNoScheme.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        } else {
            txtNoScheme.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            if (type.equals("home")) {
                viewLayout.setVisibility(View.VISIBLE);
                selectLayout.setVisibility(View.GONE);

                txtNote.setVisibility(View.VISIBLE);
                for (int i = 0; i < schemes.size(); i++) {
                    TextView txtScheme = new TextView(context);
                    txtScheme.setTextSize(18);

                    txtScheme.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet, 0, 0, 0);
                    txtScheme.setText("Buy " + schemes.get(i).quantity + " at  " + context.getResources().getString(R.string.Rs) + " " + schemes.get(i).price + " (per 1 quantity)");
                    txtScheme.setGravity(Gravity.START | Gravity.LEFT);
                    schemeLayout.addView(txtScheme);
                }

            } else {
                viewLayout.setVisibility(View.GONE);
                selectLayout.setVisibility(View.VISIBLE);

                for (int i = 0; i < schemes.size(); i++) {
                    RadioButton rButton = new RadioButton(context);
                    rButton.setTextSize(18);
                    rButton.setId(schemes.get(i).scheme_id);
                    rButton.setText(schemes.get(i).scheme + " (per 1 quantity)");
                    rGroup.addView(rButton);
                }

                RadioButton rButton = new RadioButton(context);
                rButton.setTextSize(18);
                rButton.setId(0);
                rButton.setText("No Scheme");
                rGroup.addView(rButton);

                rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selectedSchemeId = checkedId;
                        RadioButton selected = (RadioButton) rGroup.findViewById(checkedId);
                        schemeText = (String) selected.getText();
                        //Functions.showSnack(parentView, selectedSchemeId + " -> " + schemeText);
                    }
                });
            }
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return parentView;
    }

    private void init(final View parentView) {
        txtNoScheme = (TextView) parentView.findViewById(R.id.txtNoScheme);
        mainLayout = (LinearLayout) parentView.findViewById(R.id.mainLayout);
        viewLayout = (LinearLayout) parentView.findViewById(R.id.viewLayout);
        selectLayout = (LinearLayout) parentView.findViewById(R.id.selectLayout);
        schemeLayout = (LinearLayout) parentView.findViewById(R.id.schemeLayout);

        txtNote = (TextView) parentView.findViewById(R.id.txtNote);
        btnApply = (Button) parentView.findViewById(R.id.btnApply);
        rGroup = (RadioGroup) parentView.findViewById(R.id.rGroup);
    }

    @Override
    public boolean setUiBeforShow() {

        return false;
    }

    public interface OnApplyListener {
        void onApplyClick(int schemeId, String scheme);
    }

}
