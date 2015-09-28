package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sagartahelyani on 28-09-2015.
 */
public class OrderDetailsModel {

    @SerializedName("orders")
    public List<OrderModel> orders;

    @SerializedName("order_total")
    public String order_total;

    @SerializedName("payment_recived")
    public String payment_recived;

    @SerializedName("time")
    public String time;

    @SerializedName("remarks")
    public String remarks;

    @SerializedName("invoice_amount")
    public String invoice_amount;

    @SerializedName("invoice_date")
    public String invoice_date;

    @SerializedName("invoice_number")
    public String invoice_number;

    @SerializedName("retailor_name")
    public String retailor_name;

    @SerializedName("date")
    public String date;

    @SerializedName("comments")
    public String comments;

}
