package com.ilyabogdanovich.camerasampleapp.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;

import java.io.IOException;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = "IB/CameraPreview";

    private final SurfaceHolder holder;
    @Nullable
    private CameraManager.CameraInstance camera;

    public CameraPreview(Context context, CameraManager.CameraInstance camera) {
        super(context);

        this.camera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
    }

    public void setCamera(@Nullable CameraManager.CameraInstance camera) {
        this.camera = camera;
        if (this.camera != null) {
            handleSurfaceChanged();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            return;
        }

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        handleSurfaceChanged();
    }

    private void handleSurfaceChanged() {
        if (camera == null) {
            return;
        }

        if (this.holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        camera.resize();

        // start preview with new settings
        try {
            camera.setPreviewDisplay(this.holder);
            camera.startPreview();
        } catch (Exception e){
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setCamera(null);
    }
}
