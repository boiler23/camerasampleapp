package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class SaturationFilter {
    public static ColorFilter get() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
