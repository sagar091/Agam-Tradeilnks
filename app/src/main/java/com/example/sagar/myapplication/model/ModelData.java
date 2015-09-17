package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagartahelyani on 11-09-2015.
 */
public class ModelData {

    @SerializedName("products")
    public List<ModelClass> model;
}
