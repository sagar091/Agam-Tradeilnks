package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class OrderMarketingData {

    @SerializedName("orders")
    public List<OrderModel> orders;
}
