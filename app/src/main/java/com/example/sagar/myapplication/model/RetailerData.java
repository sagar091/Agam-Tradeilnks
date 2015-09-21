package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sagartahelyani on 21-09-2015.
 */
public class RetailerData {

    @SerializedName("Retailers")
    public List<RetailerClass> retailers;

}
