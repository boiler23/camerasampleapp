package com.ilyabogdanovich.camerasampleapp.managers.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.ilyabogdanovich.camerasampleapp.editor.EditorActivity;
import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;

import java.io.File;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class NavigationManagerImpl implements NavigationManager {
    private static final int GALLERY = 100;
    private final Activity activity;
    private final ImageManager imageManager;
    private Subject<Uri> gallerySubject;

    public NavigationManagerImpl(Activity activity, ImageManager imageManager) {
        this.activity = activity;
        this.imageManager = imageManager;
    }

    @Override
    public Maybe<String> editPhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        activity.startActivityForResult(galleryIntent, GALLERY);
        gallerySubject = PublishSubject.create(); // create new subject for each new call to gallery
        return gallerySubject
                .firstElement()
                .subscribeOn(Schedulers.computation())
                .map(imageManager::grabImageFromURI)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::openEditor);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY) {
            if (resultCode == Activity.RESULT_CANCELED) {
                gallerySubject.onComplete();
                return;
            }

            if (data != null) {
                gallerySubject.onNext(data.getData());
                gallerySubject.onComplete();
            }
        }
    }

    @Override
    public Maybe<String> editPhotoFromCamera(byte[] bytes) {
        return Maybe.fromCallable(() -> imageManager.grabImageFromBytes(bytes))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::openEditor);
    }

    private void openEditor(String fileName) {
        if (!fileName.isEmpty()) {
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(EditorActivity.IMAGE_FILE_ARG, fileName);
            activity.startActivity(intent);
        }
    }

    @Override
    public void shareImage(String imageFile) {
        File newFile = imageManager.getImageFile(imageFile);
        Uri contentUri = FileProvider.getUriForFile(activity, "com.ilyabogdanovich.camerasampleapp.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            activity.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
}
