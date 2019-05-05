package com.ilyabogdanovich.camerasampleapp.managers;

import io.reactivex.Observable;

public interface PermissionsManager {
    Observable<Boolean> requestCameraPermission();
    void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
