package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 02-10-2015.
 */
public class RetailerProfileModel {

    @SerializedName("shop_no")
    public String outlet;

    @SerializedName("error")
    public String error;

    @SerializedName("add_1")
    public String address2;

    @SerializedName("registred_date")
    public String registred_date;

    @SerializedName("user_login")
    public String username;

    @SerializedName("state")
    public String state;

    @SerializedName("tin")
    public String tin;

    @SerializedName("user_email")
    public String user_email;

    @SerializedName("country")
    public String country;

    @SerializedName("city")
    public String city;

    @SerializedName("pan")
    public String pan;

    @SerializedName("display_name")
    public String retailerName;

    @SerializedName("area")
    public String area;

    @SerializedName("address")
    public String address1;

    @SerializedName("mo_no")
    public String mobile1;

    @SerializedName("mo_no2")
    public String mobile2;

    @SerializedName("prefered_brand")
    public String prefered_brand;

    @SerializedName("bdate")
    public String bdate;

    @SerializedName("password")
    public String password;
}
