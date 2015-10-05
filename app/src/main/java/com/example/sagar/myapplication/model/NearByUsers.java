package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 06-10-2015.
 */
public class NearByUsers {

    @SerializedName("users")
    public List<Users> users;
}
