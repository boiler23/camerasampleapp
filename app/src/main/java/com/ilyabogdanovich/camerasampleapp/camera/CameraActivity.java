package com.ilyabogdanovich.camerasampleapp.camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;
import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;
import com.ilyabogdanovich.camerasampleapp.managers.impl.CameraManagerImpl;
import com.ilyabogdanovich.camerasampleapp.managers.impl.ImageManagerImpl;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;
import com.ilyabogdanovich.camerasampleapp.managers.impl.NavigationManagerImpl;
import com.ilyabogdanovich.camerasampleapp.managers.PermissionsManager;
import com.ilyabogdanovich.camerasampleapp.managers.impl.PermissionsManagerImpl;
import com.ilyabogdanovich.camerasampleapp.R;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class CameraActivity extends AppCompatActivity implements CameraView {
    private ViewGroup layoutPreview;
    private TextView textCameraWaiting, textNoCamera, textNoCameraPermission;
    private ImageButton buttonCapture, buttonSwitch;
    private ProgressBar progressBar;
    private PermissionsManager permissionsManager;
    private NavigationManager navigationManager;
    private CameraPreview cameraPreview = null;
    private CameraPresenter presenter;

    private Subject<CameraView> subjectCapture = PublishSubject.create();
    private Subject<CameraView> subjectOpenGallery = PublishSubject.create();
    private Subject<CameraView> subjectSwitchCamera = PublishSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_camera);

        layoutPreview = findViewById(R.id.layout_preview);
        textCameraWaiting = findViewById(R.id.text_camera_waiting);
        textNoCamera = findViewById(R.id.text_no_camera);
        textNoCameraPermission = findViewById(R.id.text_no_permission);
        progressBar = findViewById(R.id.progress_bar);
        buttonCapture = findViewById(R.id.button_capture);
        buttonSwitch = findViewById(R.id.button_switch_camera);

        findViewById(R.id.button_capture)
                .setOnClickListener(v -> subjectCapture.onNext(this));
        findViewById(R.id.button_peek_gallery)
                .setOnClickListener(v -> subjectOpenGallery.onNext(this));
        findViewById(R.id.button_switch_camera)
                .setOnClickListener(v -> subjectSwitchCamera.onNext(this));

        ImageManager imageManager = new ImageManagerImpl(this);
        CameraManager cameraManager = new CameraManagerImpl(this);
        permissionsManager = new PermissionsManagerImpl(this);
        navigationManager = new NavigationManagerImpl(this, imageManager);

        presenter = new CameraPresenter(this, cameraManager, permissionsManager, navigationManager);
        presenter.onCreate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionsManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        navigationManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showCameraButtons(boolean show) {
        buttonCapture.setVisibility(show? View.VISIBLE : View.GONE);
        buttonSwitch.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPlaceholder(boolean show) {
        textCameraWaiting.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoCameraMessage(boolean show) {
        textNoCamera.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoPermissionMessage(boolean show) {
        textNoCameraPermission.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void startCameraPreview(CameraManager.CameraInstance camera) {
        if (cameraPreview == null) {
            cameraPreview = new CameraPreview(this, camera);
            layoutPreview.addView(cameraPreview, 0);
        } else {
            cameraPreview.setCamera(camera);
        }
    }

    @Override
    public void stopCameraPreview() {
        if (cameraPreview != null) {
            cameraPreview.setCamera(null);
        }
    }

    @Override
    public Observable<CameraView> observeCaptureAction() {
        return subjectCapture;
    }

    @Override
    public Observable<CameraView> observeOpenGalleryAction() {
        return subjectOpenGallery;
    }

    @Override
    public Observable<CameraView> observeSwitchCameraAction() {
        return subjectSwitchCamera;
    }
}
