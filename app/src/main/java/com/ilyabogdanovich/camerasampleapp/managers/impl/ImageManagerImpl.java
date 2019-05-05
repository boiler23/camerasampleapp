package com.ilyabogdanovich.camerasampleapp.managers.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class ImageManagerImpl implements ImageManager {
    private static final String LOG_TAG = "IB/ImageManager";

    private final Context context;

    public ImageManagerImpl(Context context) {
        this.context = context;
    }

    @Override
    public String grabImageFromURI(@Nullable Uri uri) {
        if (uri == null) {
            return "";
        }

        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            if (in != null) {
                byte[] bytes = new byte[in.available()];
                if (in.read(bytes, 0, bytes.length) == bytes.length) {
                    return rotateAndSaveImage(bytes);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get image from URI: " + e.getMessage());
        }

        return "";
    }

    @Override
    public String grabImageFromBytes(byte[] bytes) {
        return rotateAndSaveImage(bytes);
    }

    @Override
    public String saveImage(Bitmap bitmap) {
        String fileName = Calendar.getInstance().getTimeInMillis() + ".jpg";
        File file = getImageFile(fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return fileName;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to save image to file: " + e.getMessage());
        }

        return "";
    }

    private String rotateAndSaveImage(byte[] bytes) {
        Bitmap bitmap = rotateIfNeeded(bytes);
        if (bitmap == null) {
            return "";
        }

        return saveImage(bitmap);
    }

    private int getBitmapOrientation(byte[] bytes) {
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            ExifInterface ei = new ExifInterface(in);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Failed to get exif data for image: " + e.getMessage());
        }

        return orientation;
    }

    @Nullable
    private Bitmap rotateIfNeeded(byte[] bytes) {
        int orientation = getBitmapOrientation(bytes);
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            return rotatedBitmap;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to decode bitmap: " + e);
            return null;
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public File getImageFile(String imageName) {
        File dir = new File(context.getCacheDir(), "images");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.i(LOG_TAG, "Images folder already exists");
            }
        }
        return new File(dir,  imageName);
    }

    @Nullable
    @Override
    public Bitmap openImage(String fileName) {
        try {
            File file = getImageFile(fileName);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public Bitmap extractThumbnail(Bitmap bitmap, int size) {
        return ThumbnailUtils.extractThumbnail(bitmap, size, size);
    }
}
