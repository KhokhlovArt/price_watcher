package com.khokhlov.khokhlovart.price_watcher;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityAndroidUTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.khokhlov.khokhlovart.price_watcher", appContext.getPackageName());
    }

    @Test
    public void getRes() throws Exception {

        // Context of the app under test.
//        MainActivity mAct = new MainActivity();
//        Resources r = InstrumentationRegistry.getContext().getResources();
//        assertEquals(mAct.getResources(), r);
    }
}
