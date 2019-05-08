package com.ilyabogdanovich.camerasampleapp.managers.impl;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;

import java.io.IOException;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;

public class CameraManagerImpl implements CameraManager {
    private final Activity activity;

    private static final String LOG_TAG = "IB/CameraManager";

    private static class CameraInstanceImpl implements CameraInstance {
        private final Activity activity;
        private final int cameraId;
        private final Camera camera;

        private boolean isAutoFocusSupported = false;
        @Nullable
        private List<Camera.Size> supportedPreviewSizes = null;

        private Camera.Size previewSize = null;

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

        private int getScreenRotation() {
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            return degrees;
        }

        private void configureOrientation(Camera.Parameters parameters) {
            Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int degrees = getScreenRotation();

            Log.w(LOG_TAG, "screen orientation: " + degrees);
            Log.w(LOG_TAG, "camera orientation: " + info.orientation);

            int result = (info.orientation - degrees + 360) % 360;

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
        }

        private void configureQuality(Camera.Parameters parameters) {
            parameters.setJpegQuality(90);
        }

        private void configureAutoFocus(Camera.Parameters parameters) {
            isAutoFocusSupported =
                    parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO);
            if (isAutoFocusSupported) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }

        static Camera.Size getBestPreviewSize(List<Camera.Size> sizes, int targetWidth, int targetHeight) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) targetHeight / targetWidth;

            Camera.Size bestSize = null;
            double minDiff = Double.MAX_VALUE;

            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                    continue;
                }
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    bestSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            if (bestSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        bestSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }

            return bestSize;
        }

        private void configureSize(Camera.Parameters parameters, int width, int height) {
            if (width > 0 && height > 0) {
                supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (supportedPreviewSizes != null) {
                    previewSize = getBestPreviewSize(supportedPreviewSizes, width, height);
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    parameters.setPictureSize(previewSize.width, previewSize.height);
                }
            }
        }

        CameraInstanceImpl(Activity activity, int cameraId) {
            this.activity = activity;
            this.cameraId = cameraId;
            this.camera = getCameraInstance(cameraId);
            if (this.camera != null) {
                this.supportedPreviewSizes = camera.getParameters().getSupportedPictureSizes();
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
        public Maybe<byte[]> capture() {
            if (isAutoFocusSupported) {
                return Maybe.create(emitter ->
                        camera.autoFocus(((success, c) -> {
                            if (success) {
                                takePicture(emitter);
                            } else {
                                emitter.onComplete();
                            }
                        })));
            } else {
                return Maybe.create(this::takePicture);
            }
        }

        private void takePicture(MaybeEmitter<byte[]> emitter) {
            camera.takePicture(null, null, (data, cam) -> {
                if (data != null) {
                    emitter.onSuccess(data);
                } else {
                    emitter.onError(new RuntimeException("Camera captured bytes must not be null!"));
                }
            });
        }

        @Override
        public void configure(int width, int height) {
            Camera.Parameters parameters = camera.getParameters();

            configureQuality(parameters);
            configureAutoFocus(parameters);
            configureOrientation(parameters);
            configureSize(parameters, width, height);

            camera.setParameters(parameters);
        }

        @Nullable
        @Override
        public Size measurePreview(int width, int height) {
            if (supportedPreviewSizes == null) {
                return null;
            }

            previewSize = getBestPreviewSize(supportedPreviewSizes, width, height);

            if (previewSize != null) {
                float ratio;
                if (getScreenRotation() % 180 == 0) {
                    if (previewSize.height >= previewSize.width) {
                        ratio = (float) previewSize.height / (float) previewSize.width;
                    } else {
                        ratio = (float) previewSize.width / (float) previewSize.height;
                    }
                } else {
                    if (previewSize.height >= previewSize.width) {
                        ratio = (float) previewSize.width / (float) previewSize.height;
                    } else {
                        ratio = (float) previewSize.height / (float) previewSize.width;
                    }
                }
                return new Size(width, (int) (width * ratio));
            }

            return null;
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
