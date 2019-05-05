package com.ilyabogdanovich.camerasampleapp.managers.impl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ilyabogdanovich.camerasampleapp.managers.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class PermissionsManagerImpl implements PermissionsManager {
    private static final int PERMISSIONS_REQUEST_CODE = 3;

    private final Activity activity;
    // emits true if permission granted, false otherwise
    private final Subject<Boolean> cameraPermissionSubject = ReplaySubject.createWithSize(1);

    public PermissionsManagerImpl(Activity activity) {
        this.activity = activity;
    }

    private boolean request(String[] in) {
        List<String> permissions = new ArrayList<>();

        for (String permission : in) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    activity,
                    permissions.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE);
            return true;
        }

        return false;
    }

    @Override
    public Observable<Boolean> requestCameraPermission() {
        if (!request(new String[] {Manifest.permission.CAMERA})) {
            cameraPermissionSubject.onNext(true);
        }
        return cameraPermissionSubject;
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (permissions.length > 0 && Manifest.permission.CAMERA.equals(permissions[0])) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionSubject.onNext(true);
                } else {
                    cameraPermissionSubject.onNext(false);
                }
                cameraPermissionSubject.onComplete();
            }
        }
    }
}
