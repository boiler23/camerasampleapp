package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

public class LightingFilter2 {
    public static ColorFilter get() {
        return new LightingColorFilter(0xffff4081, Color.BLACK);
    }
}
