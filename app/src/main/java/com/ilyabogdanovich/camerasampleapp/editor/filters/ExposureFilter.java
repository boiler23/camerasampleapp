package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class ExposureFilter {
    public static ColorFilter get() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                2f, 0f, 0f, 0f, 0f,
                0f, 2f, 0f, 0f, 0f,
                0f, 0f, 2f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
