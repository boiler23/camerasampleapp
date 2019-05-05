package com.ilyabogdanovich.camerasampleapp.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import com.ilyabogdanovich.camerasampleapp.editor.filters.ExposureFilter;
import com.ilyabogdanovich.camerasampleapp.editor.filters.InvertFilter;
import com.ilyabogdanovich.camerasampleapp.editor.filters.SaturationFilter;
import com.ilyabogdanovich.camerasampleapp.editor.filters.RGB2YUVFilter;
import com.ilyabogdanovich.camerasampleapp.editor.filters.RotateFilter;
import com.ilyabogdanovich.camerasampleapp.editor.filters.LightingFilter1;
import com.ilyabogdanovich.camerasampleapp.editor.filters.LightingFilter2;
import com.ilyabogdanovich.camerasampleapp.editor.filters.LightingFilter3;
import com.ilyabogdanovich.camerasampleapp.editor.filters.SepiaFilter;

public enum FilterType {
    IDENTITY(null),
    SEPIA(SepiaFilter.get()),
    EXPOSURE(ExposureFilter.get()),
    INVERT(InvertFilter.get()),
    RGB2YUV(RGB2YUVFilter.get()),
    ROTATE(RotateFilter.get()),
    SATURATION(SaturationFilter.get()),
    LIGHTING1(LightingFilter1.get()),
    LIGHTING2(LightingFilter2.get()),
    LIGHTING3(LightingFilter3.get());

    @Nullable
    private final ColorFilter filter;

    FilterType(@Nullable ColorFilter filter) {
        this.filter = filter;
    }

    @Nullable
    ColorFilter getFilter() {
        return filter;
    }

    Bitmap transform(Bitmap original) {
        if (filter == null) {
            return original;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

    static FilterType valueOf(int position) {
        for (FilterType filterType : FilterType.values()) {
            if (filterType.ordinal() == position) {
                return filterType;
            }
        }

        return IDENTITY;
    }
}
