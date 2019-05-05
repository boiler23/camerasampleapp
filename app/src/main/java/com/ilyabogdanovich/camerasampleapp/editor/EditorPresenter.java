package com.ilyabogdanovich.camerasampleapp.editor;

import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;

import io.reactivex.disposables.CompositeDisposable;

class EditorPresenter {
    private final EditorView view;
    private final EditorModel model;
    private final NavigationManager navigationManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    EditorPresenter(EditorView view,
                    EditorModel model,
                    NavigationManager navigationManager) {
        this.view = view;
        this.model = model;
        this.navigationManager = navigationManager;
    }

    void onCreate(String imageFile) {
        model.setImage(imageFile);
        view.setMainBitmap(model.getBitmap());
        view.setPreviewItems(model.getPreviewItems());
    }

    void onResume() {
        disposables.add(view
                .observePreviewClick()
                .subscribe(pos -> {
                    view.clearMainColorFilter();
                    view.setMainColorFilter(model.getColorFilter(pos));
                    view.setSelectedPreview(pos);
                }));
        disposables.add(view
                .observeShareClick()
                .subscribe(v -> navigationManager
                        .shareImage(model.saveTransformedBitmap(view.getSelectedPreview()))));
    }

    void onPause() {
        disposables.clear();
    }
}
