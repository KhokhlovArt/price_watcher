package com.khokhlov.khokhlovart.price_watcher;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static java.security.AccessController.getContext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityUnitTest {
//    MainActivity mAct;
    @Mock
    Resources fakeResources;
    @Mock
    Context fakeContext;
//
//    @Before
//    public void setUp() throws Exception {
//       mAct = Mockito.mock(MainActivity.class); //new MainActivity();
//    }
    @Test
    public void testGetRes() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(fakeContext.getResources()).thenReturn(fakeResources);
//
//        assertEquals(MainActivity.getRes(), 1);
//        assertEquals(fakeResources, 1);
//
//
//        when(mAct.getRes()).thenReturn(fakeResources);
//        assertEquals(mAct.getRes(), fakeResources);
//        assertThat(mAct.getRes(), is(not(null)));
//        assertThat(mAct.getRes(), is(not(null)));
    }

}