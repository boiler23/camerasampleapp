package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class RGB2YUVFilter {
    public static ColorFilter get() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setRGB2YUV();
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
