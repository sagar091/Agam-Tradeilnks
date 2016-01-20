package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sagar.myapplication.R;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class FilterDialog extends BaseDialog {

    View customView;
    RadioGroup radioGroup;
    String filter = "0";

    public void setOnFilterChangeListener(FilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }

    FilterChangeListener onFilterChangeListener;

    public FilterDialog(Context context, String filter) {
        super(context);
        this.filter = filter;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        customView = View.inflate(context, R.layout.filter_dialog, null);
        init(customView);

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.oneWeek) {
                    filter = "1";
                } else if (checkedId == R.id.twoWeek) {
                    filter = "2";
                } else {
                    filter = "3";
                }

                if (onFilterChangeListener != null) {
                    onFilterChangeListener.onFilter(filter);
                }
            }
        });

        return customView;
    }

    private void init(View customView) {
        radioGroup = (RadioGroup) customView.findViewById(R.id.radioGroup);

        if (filter.equals("1")) {
            ((RadioButton) customView.findViewById(R.id.oneWeek)).setChecked(true);
        } else if (filter.equals("2")) {
            ((RadioButton) customView.findViewById(R.id.twoWeek)).setChecked(true);
        } else if (filter.equals("3")) {
            ((RadioButton) customView.findViewById(R.id.threeWeek)).setChecked(true);
        } else {

        }
    }

    @Override
    public boolean setUiBeforShow() {

        return false;
    }

    public interface FilterChangeListener {
        void onFilter(String filterType);
    }
}
