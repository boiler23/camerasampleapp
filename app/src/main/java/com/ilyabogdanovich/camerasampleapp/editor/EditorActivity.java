package com.ilyabogdanovich.camerasampleapp.editor;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilyabogdanovich.camerasampleapp.managers.ImageManager;
import com.ilyabogdanovich.camerasampleapp.managers.NavigationManager;
import com.ilyabogdanovich.camerasampleapp.managers.impl.ImageManagerImpl;
import com.ilyabogdanovich.camerasampleapp.R;
import com.ilyabogdanovich.camerasampleapp.managers.impl.NavigationManagerImpl;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class EditorActivity extends AppCompatActivity implements EditorView {
    public static final String IMAGE_FILE_ARG = "image_filename";

    private EditorPresenter presenter;
    private ImageView imageView;
    private FilterListAdapter filterListAdapter;

    private Subject<EditorView> shareClickSubject = PublishSubject.create();

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

        setContentView(R.layout.activity_editor);

        findViewById(R.id.fab_share_image)
                .setOnClickListener(v -> shareClickSubject.onNext(this));

        imageView = findViewById(R.id.image_view);
        RecyclerView recyclerView = findViewById(R.id.filters_list);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        filterListAdapter = new FilterListAdapter();
        recyclerView.setAdapter(filterListAdapter);

        ImageManager imageManager = new ImageManagerImpl(this);
        NavigationManager navigationManager = new NavigationManagerImpl(this, imageManager);
        presenter = new EditorPresenter(this, new EditorModel(this, imageManager), navigationManager);

        String imageFile = "";
        if (getIntent().hasExtra(IMAGE_FILE_ARG)) {
            imageFile = getIntent().getStringExtra(IMAGE_FILE_ARG);
        }

        presenter.onCreate(imageFile);
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
    public void setMainBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void clearMainColorFilter() {
        imageView.clearColorFilter();
    }

    @Override
    public void setMainColorFilter(@Nullable ColorFilter colorFilter) {
        imageView.setColorFilter(colorFilter);
    }

    @Override
    public void setPreviewItems(FilterListItem[] previews) {
        filterListAdapter.setItems(previews);
        filterListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectedPreview(int position) {
        filterListAdapter.setSelectedItem(position);
    }

    @Override
    public int getSelectedPreview() {
        return filterListAdapter.getSelectedItem();
    }

    @Override
    public Observable<Integer> observePreviewClick() {
        return filterListAdapter.observeItemClick();
    }

    @Override
    public Observable<EditorView> observeShareClick() {
        return shareClickSubject;
    }
}
