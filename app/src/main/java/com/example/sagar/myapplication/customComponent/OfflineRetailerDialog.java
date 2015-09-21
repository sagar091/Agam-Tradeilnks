package com.example.sagar.myapplication.customComponent;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.model.RetailerData;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.rey.material.app.Dialog;

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
    SearchView searchView;

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

        offlineListView = (ListView) customView.findViewById(R.id.offlineList);
        searchView = (SearchView) customView.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    offlineListView.clearTextFilter();
                } else {
                    offlineListView.setFilterText(newText);
                }

                return true;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Search Retailer");

        ArrayList<String> retailers = new ArrayList<>();
        for (int i = 0; i < retailerData.retailers.size(); i++) {
            retailers.add(retailerData.retailers.get(i).retailer.retailerName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, retailers);

        offlineListView.setAdapter(adapter);
        offlineListView.setTextFilterEnabled(true);

        offlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRetailerId = retailerData.retailers.get(position).retailer.retailerId;
                selectRetailerName = retailerData.retailers.get(position).retailer.retailerName;
                if (onSelectRetailerListner != null) {
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
