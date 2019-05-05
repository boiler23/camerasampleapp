package com.ilyabogdanovich.camerasampleapp.editor;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ilyabogdanovich.camerasampleapp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class EditorActivityTest {
    @Rule
    public final IntentsTestRule<EditorActivity> rule =
            new IntentsTestRule<>(EditorActivity.class, false, false);

    @Before
    public void setUp() throws IOException {
        File dir = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "images");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        File img = new File(dir, "test.jpg");
        try (FileOutputStream out = new FileOutputStream(img)) {
            Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            rule.launchActivity(new Intent().putExtra(EditorActivity.IMAGE_FILE_ARG, "test.jpg"));
        }
    }

    @Test
    public void applyFilter() {
        onView(withId(R.id.filters_list))
                .perform(actionOnItemAtPosition(3, click()));
        onView(withId(R.id.fab_share_image))
                .perform(click());

        intended(hasAction(Intent.ACTION_CHOOSER));
    }
}
