package com.masaworld.catmap;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.util.MutableBoolean;

import com.masaworld.catmap.data.DataDripper;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class DataDripperTest {

    private LifecycleRegistry lifecycleRegistry;
    private DataDripper<String> dataDripper;

    @Before
    public void init() {
        lifecycleRegistry = new LifecycleRegistry(mock(LifecycleOwner.class));
        dataDripper = new DataDripper<>();
    }

    private void addData() {
        dataDripper.add("a");
        dataDripper.add("b");
        dataDripper.add("c");
    }

    @Test
    public void dripAllTest() {
        addData();
        final StringBuilder builder = new StringBuilder();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        dataDripper.observe(lifecycleRegistry, builder::append);

        assertEquals(builder.toString(), "");

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        assertEquals(builder.toString(), "abc");
    }

    @Test
    public void addTest() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        MutableBoolean notified = new MutableBoolean(false);

        dataDripper.observe(lifecycleRegistry, data -> notified.value = true);
        dataDripper.add("ppp");
        assertTrue(notified.value);
    }

    @Test
    public void newObserverTest() {
        addData();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        dataDripper.observe(lifecycleRegistry, System.out::println);

        final StringBuilder builder = new StringBuilder();
        dataDripper.observe(lifecycleRegistry, builder::append);
        assertEquals(builder.toString(), "abc");
    }

    @Test
    public void notDrippingWhenInactiveState() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        final MutableBoolean notified = new MutableBoolean(false);
        dataDripper.observe(lifecycleRegistry, data -> notified.value = true);

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        dataDripper.add("popo");
        assertFalse(notified.value);
    }
}
