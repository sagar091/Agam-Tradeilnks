package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.model.RetailerData;
import com.flyco.animation.Attention.RubberBand;
import com.flyco.animation.Attention.Swing;
import com.flyco.animation.Attention.Tada;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FallEnter.FallEnter;
import com.flyco.animation.Jelly;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class OfflineRetailerDialog extends BaseDialog {

    View customView;
    View parentView;
    RetailerData retailerData;
    private String selectRetailerId, selectRetailerName;
    ListView offlineListView;
    EditText edtSearch;

    public void setOnSelectRetailerListner(OfflineRetailerDialog.onSelectRetailerListner onSelectRetailerListner) {
        this.onSelectRetailerListner = onSelectRetailerListner;
    }

    onSelectRetailerListner onSelectRetailerListner;

    public OfflineRetailerDialog(Context context, RetailerData retailerData) {
        super(context);
        this.retailerData = retailerData;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        customView = View.inflate(context, R.layout.offline_retailor_list, null);
        init(customView);

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return customView;
    }

    private void init(View customView) {
        parentView = (View) findViewById(android.R.id.content);

        edtSearch = (EditText) customView.findViewById(R.id.edtSearch);
        offlineListView = (ListView) customView.findViewById(R.id.offlineList);

        ArrayList<String> retailers = new ArrayList<>();
        for (int i = 0; i < retailerData.retailers.size(); i++) {
            retailers.add(retailerData.retailers.get(i).retailer.retailerName);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, retailers);

        offlineListView.setAdapter(adapter);
        offlineListView.setTextFilterEnabled(true);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0)
                    adapter.getFilter().filter(s.toString().trim());
                else
                    offlineListView.clearTextFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        offlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRetailerName = parent.getItemAtPosition(position) + "";
                for (int i = 0; i < retailerData.retailers.size(); i++) {
                    if (retailerData.retailers.get(i).retailer.retailerName.equals(selectRetailerName)) {
                        selectRetailerId = retailerData.retailers.get(i).retailer.retailerId;
                        break;
                    }
                }
                Log.e(selectRetailerId, selectRetailerName);

                if (onSelectRetailerListner != null) {
                    dismiss();
                    onSelectRetailerListner.OnClickRetailer(selectRetailerId, selectRetailerName);
                }
            }
        });

    }

    @Override
    public boolean setUiBeforShow() {
        return false;
    }


    public interface onSelectRetailerListner {
        public void OnClickRetailer(String selectRetailerId, String selectRetailerName);
    }
}
