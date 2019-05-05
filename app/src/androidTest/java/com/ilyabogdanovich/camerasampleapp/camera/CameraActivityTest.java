package com.ilyabogdanovich.camerasampleapp.camera;

import android.Manifest;
import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.ilyabogdanovich.camerasampleapp.R;
import com.ilyabogdanovich.camerasampleapp.editor.EditorActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CameraActivityTest {
    @Rule
    public final IntentsTestRule<CameraActivity> rule =
            new IntentsTestRule<>(CameraActivity.class, false, false);
    @Rule
    public final GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void setUp() {
        rule.launchActivity(new Intent());
    }

    @Test
    public void startCamera() {
        onView(withId(R.id.button_capture))
                .check(matches(isDisplayed()))
                .perform(click());

        // todo use idling resource (need some time to process image)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasComponent(EditorActivity.class.getName()));
    }
}
