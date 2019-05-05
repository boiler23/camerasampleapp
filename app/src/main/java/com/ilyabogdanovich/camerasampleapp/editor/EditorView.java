package com.ilyabogdanovich.camerasampleapp.editor;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;

import androidx.annotation.Nullable;

import io.reactivex.Observable;

public interface EditorView {
    void setMainBitmap(Bitmap bitmap);
    void clearMainColorFilter();
    void setMainColorFilter(@Nullable ColorFilter colorFilter);
    void setPreviewItems(FilterListItem[] previews);
    void setSelectedPreview(int position);
    int getSelectedPreview();
    Observable<Integer> observePreviewClick();
    Observable<EditorView> observeShareClick();
}
