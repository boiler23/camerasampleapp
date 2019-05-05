package com.ilyabogdanovich.camerasampleapp.managers.impl;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;

import java.io.IOException;

import io.reactivex.Single;

public class CameraManagerImpl implements CameraManager {
    private final Activity activity;

    private static final String LOG_TAG = "IB/CameraManager";

    private static class CameraInstanceImpl implements CameraInstance {
        private final Activity activity;
        private final int cameraId;
        private final Camera camera;

        /** A safe way to get an instance of the Camera object. */
        private static Camera getCameraInstance(int cameraId) {
            Camera c = null;
            try {
                c = Camera.open(cameraId);
            }
            catch (Exception e) {
                // Camera is not available (in use or does not exist)
                Log.w(LOG_TAG, "Camera is not available: " + e.getMessage());
            }
            return c;
        }

        private static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
            Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            Log.w(LOG_TAG, "screen orientation: " + degrees);
            Log.w(LOG_TAG, "camera orientation: " + info.orientation);

            int result = (info.orientation - degrees + 360) % 360;

            Camera.Parameters parameters = camera.getParameters();
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                parameters.setRotation(result);
                result = (result + 180) % 360;
                if (degrees % 180 != 0) {
                    parameters.setRotation(result);
                }
                camera.setDisplayOrientation(result);
            } else {
                camera.setDisplayOrientation(result);
                parameters.setRotation(result);
            }

            parameters.setJpegQuality(90);
            Camera.Size previewSize = parameters.getPreviewSize();
            parameters.setPictureSize(previewSize.width, previewSize.height);
            camera.setParameters(parameters);
        }

        CameraInstanceImpl(Activity activity, int cameraId) {
            this.activity = activity;
            this.cameraId = cameraId;
            this.camera = getCameraInstance(cameraId);
            if (this.camera != null) {
                resize();
            }
        }

        @Override
        public void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException {
            camera.setPreviewDisplay(surfaceHolder);
        }

        @Override
        public void startPreview() {
            camera.startPreview();
        }

        @Override
        public void stopPreview() {
            camera.stopPreview();
        }

        @Override
        public Single<byte[]> capture() {
            return Single.create(emitter ->
                camera.takePicture(null, null, (data, c) -> {
                    if (data != null) {
                        emitter.onSuccess(data);
                    } else {
                        emitter.onError(new RuntimeException("Camera captured bytes must not be null!"));
                    }
                })
            );
        }

        @Override
        public void resize() {
            setCameraDisplayOrientation(activity, cameraId, camera);
        }

        @Override
        public void release() {
            if (camera != null) {
                camera.release();
            }
        }
    }

    public CameraManagerImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean checkCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public CameraInstance obtain(int cameraId) {
        return new CameraInstanceImpl(activity, cameraId);
    }
}
