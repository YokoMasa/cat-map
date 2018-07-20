package com.masaworld.catmap.data;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DataDripper<T> implements LifecycleObserver {

    private Lifecycle lifecycle;
    private Observer<T> observer;
    private boolean isObserverActive;
    private boolean dripOnStart;
    private List<T> data = new ArrayList<>();

    public void observe(@NonNull Lifecycle lifecycle, @NonNull Observer<T> observer) {
        lifecycle.addObserver(this);
        this.lifecycle = lifecycle;
        this.observer = observer;
        isObserverActive = true;
        dripAll();
    }

    private boolean canDrip() {
        return isObserverActive && lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }

    private void dripAll() {
        if (canDrip()) {
            dripList(this.data);
        } else {
            dripOnStart = true;
        }
    }

    private void dripList(List<T> dataList) {
        for (T data : dataList) {
            observer.onDrip(data);
        }
    }

    public void clear() {
        data.clear();
    }

    public void add(T data) {
        this.data.add(data);
        if (canDrip()) {
            this.observer.onDrip(data);
        }
    }

    public void add(List<T> dataList) {
        this.data.addAll(dataList);
        if (canDrip()) {
            dripList(dataList);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (dripOnStart) {
            dripList(this.data);
            dripOnStart = false;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        lifecycle = null;
        observer = null;
        isObserverActive = false;
    }

    public interface Observer<T> {
        public void onDrip(T data);
    }

}
