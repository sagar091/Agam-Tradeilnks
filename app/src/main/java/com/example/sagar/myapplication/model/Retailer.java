package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 21-09-2015.
 */
public class Retailer {
    @SerializedName("retailer_username")
    public String retailerName;

    @SerializedName("retailer_id")
    public String retailerId;
}
