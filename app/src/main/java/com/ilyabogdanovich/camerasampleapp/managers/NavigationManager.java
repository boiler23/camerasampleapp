package com.ilyabogdanovich.camerasampleapp.managers;

import android.content.Intent;

import androidx.annotation.Nullable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;

public interface NavigationManager {
    @CheckReturnValue
    Maybe<String> editPhotoFromGallery();
    @CheckReturnValue
    Maybe<String> editPhotoFromCamera(byte[] bytes);
    void shareImage(String imagePath);
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
