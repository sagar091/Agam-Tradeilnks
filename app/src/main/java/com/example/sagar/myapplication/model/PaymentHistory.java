package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 04-10-2015.
 */
public class PaymentHistory {

    @SerializedName("data")
    public List<OrderClass> data;
}
