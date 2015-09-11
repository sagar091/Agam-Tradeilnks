package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 11-09-2015.
 */
public class CompanyData {

    @SerializedName("cats")
    public ArrayList<Company> company;

}
