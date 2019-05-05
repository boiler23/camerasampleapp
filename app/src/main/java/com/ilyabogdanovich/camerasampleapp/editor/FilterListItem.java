package com.ilyabogdanovich.camerasampleapp.editor;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;

import androidx.annotation.Nullable;

class FilterListItem {
    @Nullable
    final Bitmap bitmap;
    @Nullable
    final ColorFilter colorFilter;
    final String name;

    FilterListItem(@Nullable Bitmap bitmap, @Nullable ColorFilter colorFilter, String name) {
        this.bitmap = bitmap;
        this.colorFilter = colorFilter;
        this.name = name;
    }
}
