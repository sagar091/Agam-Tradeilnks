package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sagartahelyani on 28-09-2015.
 */
public class Products {

    @SerializedName("orders")
    public List<OrderModel> orders;

    @SerializedName("products")
    public List<ProductModel> product;
}
