package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class OrderClass {

    @SerializedName("order_total")
    public String order_total;

    @SerializedName("time")
    public String time;

    @SerializedName("retailor_id")
    public String retailor_id;

    @SerializedName("date")
    public String date;

    @SerializedName("order_date")
    public String order_date;

    @SerializedName("order_id")
    public String order_id;

    @SerializedName("dilivery_pending")
    public String dilivery_pending;

    @SerializedName("payment_pending")
    public String payment_pending;
}
