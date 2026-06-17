package com.example.datalibrary.utils;

import android.content.Context;

/**
 * Created by l on 2017/8/14.
 */

public class PreferencesManager extends BasePreferencesManager {


    private static final String RGB_DEPTH = "rgbDepth";
    private static final String RGB_NIR_DEPTH = "rgbNirDepth";
    private static final String TYPE = "type";

    private static PreferencesManager instance = null;

    protected PreferencesManager(Context context) {
        super(context);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void setType(int type) {
        setInt(TYPE, "type", type);
    }

    public int getType() {
        return getInt(TYPE, "type", 0);
    }

    public void setRgbDepth(int rgbDepth) {
        setInt(RGB_DEPTH, "rgbDepth", rgbDepth);
    }

    public int getRgbDepth() {
        return getInt(RGB_DEPTH, "rgbDepth", 0);
    }

    public void setRgbNirDepth(int rgbNirDepth) {
        setInt(RGB_NIR_DEPTH, "rgbNirDepth", rgbNirDepth);
    }

    public int getRgbNirDepth() {
        return getInt(RGB_NIR_DEPTH, "rgbNirDepth", 0);
    }

}
