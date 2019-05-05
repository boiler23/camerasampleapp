package com.ilyabogdanovich.camerasampleapp.editor.filters;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class InvertFilter {
    public static ColorFilter get() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                -1f,  0f,  0f, 0f, 255f,
                 0f, -1f,  0f, 0f, 255f,
                 0f,  0f, -1f, 0f, 255f,
                 0f,  0f,  0f, 1f, 255f
        });
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
