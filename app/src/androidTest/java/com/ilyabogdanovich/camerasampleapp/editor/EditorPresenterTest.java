package com.ilyabogdanovich.camerasampleapp.editor;

import androidx.test.core.app.ApplicationProvider;

import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;
import com.ilyabogdanovich.camerasampleapp.managers.impl.ImageManagerImpl;

import org.junit.Test;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditorPresenterTest {
    @Test
    public void testShare() {
        EditorView view = mock(EditorView.class);
        when(view.getSelectedPreview())
                .thenReturn(1);
        when(view.observeShareClick())
                .thenReturn(Observable.just(view));
        when(view.observePreviewClick())
                .thenReturn(Observable.never());

        NavigationManager navigationManager = mock(NavigationManager.class);
        ImageManager imageManager = new ImageManagerImpl(ApplicationProvider.getApplicationContext());

        EditorModel model = new EditorModel(ApplicationProvider.getApplicationContext(), imageManager);
        EditorPresenter presenter = new EditorPresenter(view, model, navigationManager);
        presenter.onCreate("test.jpg");
        presenter.onResume();

        verify(navigationManager, times(1))
                .shareImage(matches(".*\\.jpg"));

        presenter.onPause();
    }
}
