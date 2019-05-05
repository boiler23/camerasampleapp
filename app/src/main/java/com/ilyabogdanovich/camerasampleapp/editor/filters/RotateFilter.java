package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class RotateFilter {
    public static ColorFilter get() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setRotate(0, 30f);
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
