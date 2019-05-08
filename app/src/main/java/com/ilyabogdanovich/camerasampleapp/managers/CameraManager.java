package com.ilyabogdanovich.camerasampleapp.managers;

import android.view.SurfaceHolder;

import java.io.IOException;

import io.reactivex.Maybe;

public interface CameraManager {
    int CAMERA_BACK_ID = 0;
    int CAMERA_FACE_ID = 1;

    class Size {
        public final int width, height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    interface CameraInstance {
        void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException;
        void startPreview();
        void stopPreview();
        Maybe<byte[]> capture();
        void configure(int width, int height);
        Size measurePreview(int width, int height);
        void release();
    }

    boolean checkCameraExists();

    CameraInstance obtain(int cameraId);
}
