package com.ilyabogdanovich.camerasampleapp.managers;

import android.view.SurfaceHolder;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface CameraManager {
    int CAMERA_BACK_ID = 0;
    int CAMERA_FACE_ID = 1;

    interface CameraInstance {
        void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException;
        void startPreview();
        void stopPreview();
        Maybe<byte[]> capture();
        void resize();
        void release();
    }

    boolean checkCameraExists();

    CameraInstance obtain(int cameraId);
}
