package com.ilyabogdanovich.camerasampleapp.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.util.TypedValue;

import androidx.annotation.Nullable;

import com.ilyabogdanovich.camerasampleapp.R;
import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;

class EditorModel {
    private static final int THUMB_SIZE_DP = 64;

    private final Context context;
    private final ImageManager imageManager;

    private FilterListItem[] items;
    private Bitmap bitmap;

    EditorModel(Context context, ImageManager imageManager) {
        this.context = context;
        this.imageManager = imageManager;
    }

    void setImage(String imageFile) {
        bitmap = imageManager.openImage(imageFile);
        String[] filterNames = context.getResources().getStringArray(R.array.filter_names);

        int thumbSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, THUMB_SIZE_DP, context.getResources().getDisplayMetrics());
        Bitmap thumbImage = imageManager.extractThumbnail(bitmap, thumbSize);
        items = new FilterListItem[FilterType.values().length];
        for (FilterType filterType : FilterType.values()) {
            int pos = filterType.ordinal();
            Bitmap bitmap = thumbImage.copy(Bitmap.Config.RGB_565, true);
            items[pos] = new FilterListItem(bitmap, filterType.getFilter(), filterNames[pos]);
        }
    }

    String saveTransformedBitmap(int pos) {
        FilterType filterType = FilterType.valueOf(pos);
        return imageManager.saveImage(filterType.transform(bitmap));
    }

    @Nullable
    ColorFilter getColorFilter(int pos) {
        return FilterType.valueOf(pos).getFilter();
    }

    Bitmap getBitmap() {
        return bitmap;
    }

    FilterListItem[] getPreviewItems() {
        return items;
    }
}
