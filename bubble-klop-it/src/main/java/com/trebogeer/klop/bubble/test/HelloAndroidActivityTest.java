package com.trebogeer.klop.bubble.test;

import android.test.ActivityInstrumentationTestCase2;
import com.trebogeer.klop.bubble.BubblesActivity;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<BubblesActivity> {

    public HelloAndroidActivityTest() {
        super(BubblesActivity.class);
    }

    public void testActivity() {
        BubblesActivity activity = getActivity();
        assertNotNull(activity);
    }
}

