package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

public class LightingFilter1 {
    public static ColorFilter get() {
        return new LightingColorFilter(0xffff4081, 0xffff4081);
    }
}
