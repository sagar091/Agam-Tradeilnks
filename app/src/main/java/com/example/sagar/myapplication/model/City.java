package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 29-09-2015.
 */
public class City {

    @SerializedName("data")
    public List<CityModel> cityModels;
}
