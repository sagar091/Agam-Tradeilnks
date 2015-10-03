package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 11-09-2015.
 */
public class UserProfile {

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("password")
    public String password;

    @SerializedName("name")
    public String name;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("prefered_brand")
    public String prefered_brand;

    @SerializedName("is_new")
    public String is_new;
}
