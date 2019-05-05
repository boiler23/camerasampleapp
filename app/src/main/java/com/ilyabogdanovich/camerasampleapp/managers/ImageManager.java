package com.ilyabogdanovich.camerasampleapp.managers;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.File;

public interface ImageManager {
    String grabImageFromURI(@Nullable Uri uri);
    String grabImageFromBytes(byte[] bytes);
    String saveImage(Bitmap bitmap);

    File getImageFile(String imageName);
    Bitmap openImage(String fileName);
    Bitmap extractThumbnail(Bitmap bitmap, int size);
}
