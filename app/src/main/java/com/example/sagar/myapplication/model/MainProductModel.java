package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 03-10-2015.
 */
public class MainProductModel {

    @SerializedName("name")
    public String name;

    @SerializedName("pid")
    public String pid;

    @SerializedName("product_image")
    public String product_image;

    @SerializedName("featured_details")
    public FeaturedModel featuredData;
}
