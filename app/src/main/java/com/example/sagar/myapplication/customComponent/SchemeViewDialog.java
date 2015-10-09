package com.example.sagar.myapplication.customComponent;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.helper.ComplexPreferences;
import com.example.sagar.myapplication.helper.Constants;
import com.example.sagar.myapplication.helper.Functions;
import com.example.sagar.myapplication.helper.HttpRequest;
import com.example.sagar.myapplication.model.Scheme;
import com.example.sagar.myapplication.model.UserProfile;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class SchemeViewDialog extends BaseDialog {

    View parentView;
    private LinearLayout schemeLayout;
    private TextView txtNote;
    List<Scheme> schemes;
    String type;

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

        if (type.equals("home")) {
            txtNote.setVisibility(View.VISIBLE);
            for (int i = 0; i < schemes.size(); i++) {
                TextView txtScheme = new TextView(context);
                txtScheme.setTextSize(18);

                txtScheme.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet, 0, 0, 0);
                txtScheme.setText("Buy " + schemes.get(i).quantity + " at  " + context.getResources().getString(R.string.Rs) + " " + schemes.get(i).price);
                schemeLayout.addView(txtScheme);
            }

        } else {
            txtNote.setVisibility(View.GONE);
            RadioGroup rGroup = new RadioGroup(context);
            for (int i = 0; i < schemes.size(); i++) {
                RadioButton rButton = new RadioButton(context);
                rButton.setTextSize(18);
                rButton.setId(schemes.get(i).scheme_id);
                rButton.setText(schemes.get(i).scheme);
                rGroup.addView(rButton);
            }
            schemeLayout.addView(rGroup);

            rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Toast.makeText(context, checkedId + "", Toast.LENGTH_LONG).show();
                }
            });
        }

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return parentView;
    }

    private void init(final View parentView) {
        schemeLayout = (LinearLayout) parentView.findViewById(R.id.schemeLayout);
        txtNote = (TextView) parentView.findViewById(R.id.txtNote);
    }

    @Override
    public boolean setUiBeforShow() {

        return false;
    }

}
