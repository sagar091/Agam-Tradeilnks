package com.example.sagar.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 03-10-2015.
 */
public class FeaturedModel {
    @SerializedName("screen_size")
    public String display;

    @SerializedName("os")
    public String os;

    @SerializedName("camera")
    public String camera;

    @SerializedName("ram_rom")
    public String ram_rom;

    @SerializedName("expandable_slot")
    public String expandable_slot;

    @SerializedName("processor")
    public String processor;

    @SerializedName("battery")
    public String battery;

    @SerializedName("flash")
    public String flash;

    @SerializedName("dp")
    public String dp;

    @SerializedName("mrp")
    public String mrp;

    @SerializedName("color")
    public String color;

    @SerializedName("bt")
    public String bt;

    @SerializedName("audio_video")
    public String audio_video;

    @SerializedName("fm")
    public String fm;

    @SerializedName("special_feature")
    public String special_feature;
}
