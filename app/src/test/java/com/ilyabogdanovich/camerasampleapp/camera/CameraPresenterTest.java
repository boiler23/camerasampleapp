package com.ilyabogdanovich.camerasampleapp.camera;

import com.ilyabogdanovich.camerasampleapp.managers.CameraManager;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;
import com.ilyabogdanovich.camerasampleapp.managers.PermissionsManager;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CameraPresenterTest {
    private final CameraView mockView = new CameraView() {
        @Override
        public void showCameraButtons(boolean show) { }

        @Override
        public void showPlaceholder(boolean show) { }

        @Override
        public void showNoCameraMessage(boolean show) { }

        @Override
        public void showNoPermissionMessage(boolean show) { }

        @Override
        public void showProgress(boolean show) { }

        @Override
        public void startCameraPreview(CameraManager.CameraInstance camera) { }

        @Override
        public void stopCameraPreview() { }

        @Override
        public Observable<CameraView> observeCaptureAction() { return Observable.never(); }

        @Override
        public Observable<CameraView> observeOpenGalleryAction() { return Observable.never(); }

        @Override
        public Observable<CameraView> observeSwitchCameraAction() { return Observable.never(); }
    };

    @Test
    public void testNoCamera() {
        CameraView view = spy(mockView);

        CameraManager cameraManager = mock(CameraManager.class);
        when(cameraManager.checkCameraExists())
                .thenReturn(false);

        PermissionsManager permissionsManager = mock(PermissionsManager.class);
        NavigationManager navigationManager = mock(NavigationManager.class);

        CameraPresenter presenter =
                new CameraPresenter(view, cameraManager, permissionsManager, navigationManager);
        presenter.onCreate();
        presenter.onResume();

        verify(view, times(1)).showPlaceholder(false);
        verify(view, times(1)).showNoCameraMessage(true);
        verify(view, times(0)).showNoPermissionMessage(true);
        verify(view, times(0)).startCameraPreview(any());
        verify(view, times(0)).showCameraButtons(true);
        verify(permissionsManager, times(0)).requestCameraPermission();

        presenter.onPause();
        presenter.onDestroy();
    }

    @Test
    public void testNoPermission() {
        CameraView view = spy(mockView);

        CameraManager cameraManager = mock(CameraManager.class);
        when(cameraManager.checkCameraExists())
                .thenReturn(true);

        PermissionsManager permissionsManager = mock(PermissionsManager.class);
        when(permissionsManager.requestCameraPermission())
                .thenReturn(Observable.just(false));

        NavigationManager navigationManager = mock(NavigationManager.class);

        CameraPresenter presenter =
                new CameraPresenter(view, cameraManager, permissionsManager, navigationManager);
        presenter.onCreate();
        presenter.onResume();

        verify(view, times(1)).showPlaceholder(false);
        verify(view, times(0)).showNoCameraMessage(true);
        verify(view, times(1)).showNoPermissionMessage(true);
        verify(view, times(0)).startCameraPreview(any());
        verify(view, times(0)).showCameraButtons(true);
        verify(permissionsManager, times(1)).requestCameraPermission();

        presenter.onPause();
        presenter.onDestroy();
    }

    @Test
    public void testCameraExistsAndGrantedPermission() {
        CameraView view = spy(mockView);

        CameraManager.CameraInstance camera = mock(CameraManager.CameraInstance.class);
        CameraManager cameraManager = mock(CameraManager.class);
        when(cameraManager.checkCameraExists())
                .thenReturn(true);
        when(cameraManager.obtain(0))
                .thenReturn(camera);

        PermissionsManager permissionsManager = mock(PermissionsManager.class);
        when(permissionsManager.requestCameraPermission())
                .thenReturn(Observable.just(true));

        NavigationManager navigationManager = mock(NavigationManager.class);

        CameraPresenter presenter =
                new CameraPresenter(view, cameraManager, permissionsManager, navigationManager);
        presenter.onCreate();
        presenter.onResume();

        verify(view, times(1)).showPlaceholder(false);
        verify(view, times(0)).showNoCameraMessage(true);
        verify(view, times(0)).showNoPermissionMessage(true);
        verify(view, times(1)).startCameraPreview(camera);
        verify(view, times(1)).showCameraButtons(true);
        verify(permissionsManager, times(1)).requestCameraPermission();

        presenter.onPause();
        presenter.onDestroy();
    }

    @Test
    public void testCapture() {
        CameraView view = spy(mockView);
        when(view.observeCaptureAction())
                .thenReturn(Observable.just(view));

        CameraManager.CameraInstance camera = mock(CameraManager.CameraInstance.class);
        when(camera.capture()).thenReturn(Single.just(new byte[0]));

        CameraManager cameraManager = mock(CameraManager.class);
        when(cameraManager.checkCameraExists())
                .thenReturn(true);
        when(cameraManager.obtain(0))
                .thenReturn(camera);

        PermissionsManager permissionsManager = mock(PermissionsManager.class);
        when(permissionsManager.requestCameraPermission())
                .thenReturn(Observable.just(true));

        NavigationManager navigationManager = mock(NavigationManager.class);
        when(navigationManager.editPhotoFromCamera(any()))
                .thenReturn(Single.just("test.jpg"));

        CameraPresenter presenter =
                new CameraPresenter(view, cameraManager, permissionsManager, navigationManager);
        presenter.onCreate();
        presenter.onResume();

        //noinspection ResultOfMethodCallIgnored
        verify(navigationManager, times(1)).editPhotoFromCamera(any());

        presenter.onPause();
        presenter.onDestroy();
    }
}
