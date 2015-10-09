package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 06-10-2015.
 */
public class Scheme {

    @SerializedName("id")
    public String id;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("price")
    public String price;

    @SerializedName("scheme")
    public String scheme;

    @SerializedName("scheme_id")
    public int scheme_id;

}

