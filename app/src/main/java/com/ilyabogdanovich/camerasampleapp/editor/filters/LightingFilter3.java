package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

public class LightingFilter3 {
    public static ColorFilter get() {
        return new LightingColorFilter(Color.WHITE, 0xffff4081);
    }
}
