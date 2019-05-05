package com.ilyabogdanovich.camerasampleapp.camera;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;

import io.reactivex.Observable;

public interface CameraView {
    void showCameraButtons(boolean show);
    void showPlaceholder(boolean show);
    void showNoCameraMessage(boolean show);
    void showNoPermissionMessage(boolean show);
    void showProgress(boolean show);
    void startCameraPreview(CameraManager.CameraInstance camera);
    void stopCameraPreview();

    Observable<CameraView> observeCaptureAction();
    Observable<CameraView> observeOpenGalleryAction();
    Observable<CameraView> observeSwitchCameraAction();
}
