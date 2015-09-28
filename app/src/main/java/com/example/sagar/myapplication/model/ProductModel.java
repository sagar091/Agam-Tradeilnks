package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 28-09-2015.
 */
public class ProductModel {

    @SerializedName("product_id")
    public String product_id;

    @SerializedName("product_name")
    public String product_name;

    @SerializedName("qty")
    public String qty;
}
