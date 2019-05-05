package com.ilyabogdanovich.camerasampleapp.camera;

import androidx.annotation.Nullable;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;
import com.ilyabogdanovich.camerasampleapp.managers.PermissionsManager;

import io.reactivex.disposables.CompositeDisposable;

class CameraPresenter {
    private final CameraView view;
    private final CameraManager cameraManager;
    private final PermissionsManager permissionsManager;
    private final NavigationManager navigationManager;
    private final CompositeDisposable disposableOnPause = new CompositeDisposable();
    private final CompositeDisposable disposableOnDestroy = new CompositeDisposable();
    @Nullable
    private CameraManager.CameraInstance camera = null;
    private int cameraId = CameraManager.CAMERA_BACK_ID;
    private boolean hasPermission = false, isResumed = false;

    CameraPresenter(CameraView view,
                    CameraManager cameraManager,
                    PermissionsManager permissionsManager,
                    NavigationManager navigationManager) {
        this.view = view;
        this.cameraManager = cameraManager;
        this.permissionsManager = permissionsManager;
        this.navigationManager = navigationManager;
    }

    private void capture() {
        if (camera != null) {
            disposableOnPause.add(camera
                    .capture()
                    .subscribe(this::onPictureTaken));
        }
    }

    private void onPictureTaken(byte[] bytes) {
        disposableOnPause.add(navigationManager
                .editPhotoFromCamera(bytes)
                .doOnSubscribe(d -> view.showProgress(true))
                .doFinally(() -> view.showProgress(false))
                .subscribe());
    }

    private void openGallery() {
        disposableOnDestroy.add(navigationManager
                .editPhotoFromGallery()
                .doOnSubscribe(d -> view.showProgress(true))
                .doFinally(() -> view.showProgress(false))
                .subscribe());
    }

    private void switchCamera() {
        cameraId = cameraId == CameraManager.CAMERA_BACK_ID ?
                CameraManager.CAMERA_FACE_ID : CameraManager.CAMERA_BACK_ID;
        restartPreview();
    }

    private void restartPreview() {
        view.stopCameraPreview();
        if (camera != null) {
            camera.release();
        }
        camera = cameraManager.obtain(cameraId);
        view.startCameraPreview(camera);
    }

    void onCreate() {
        if (!cameraManager.checkCameraExists()) {
            view.showPlaceholder(false);
            view.showNoCameraMessage(true);
        } else {
            disposableOnDestroy.add(permissionsManager
                    .requestCameraPermission()
                    .subscribe(this::onCameraPermissionRequestComplete));
        }

        disposableOnDestroy.add(view
                .observeOpenGalleryAction()
                .subscribe(view -> openGallery()));
    }

    void onResume() {
        isResumed = true;
        tryStartCamera();

        disposableOnPause.add(view
                .observeCaptureAction()
                .subscribe(view -> capture()));
        disposableOnPause.add(view
                .observeSwitchCameraAction()
                .subscribe(view -> switchCamera()));
    }

    void onPause() {
        view.stopCameraPreview();
        if (camera != null) {
            camera.release();
            camera = null;
        }

        disposableOnPause.clear();
    }

    void onDestroy() {
        disposableOnDestroy.clear();
    }

    private void tryStartCamera() {
        if (hasPermission && isResumed) {
            camera = cameraManager.obtain(cameraId);
            view.startCameraPreview(camera);
        }
    }

    private void onCameraPermissionRequestComplete(boolean granted) {
        view.showPlaceholder(false);
        if (granted) {
            hasPermission = true;
            tryStartCamera();
            view.showCameraButtons(true);
        } else {
            view.showNoPermissionMessage(true);
        }
    }
}
