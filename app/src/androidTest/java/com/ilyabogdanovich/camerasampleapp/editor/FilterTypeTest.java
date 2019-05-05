package com.ilyabogdanovich.camerasampleapp.editor;

import android.graphics.Bitmap;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class FilterTypeTest {
    private boolean areBitmapsEqual(Bitmap expected, Bitmap actual) {
        ByteBuffer buffer1 = ByteBuffer.allocate(expected.getHeight() * expected.getRowBytes());
        expected.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(actual.getHeight() * actual.getRowBytes());
        actual.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    @Test
    public void transformIdentity() {
        Bitmap original = Bitmap.createBitmap(64, 64, Bitmap.Config.RGB_565);
        Bitmap transformed = FilterType.IDENTITY.transform(original);

        assertThat(areBitmapsEqual(original, transformed), is(true));
    }

    @Test
    public void transformInvert() {
        Bitmap original = Bitmap.createBitmap(64, 64, Bitmap.Config.RGB_565);
        Bitmap transformed = FilterType.INVERT.transform(original);

        assertThat(areBitmapsEqual(original, transformed), is(false));
    }
}
